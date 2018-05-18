package de.seidfred.accountservice.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Splitter;

import de.seidfred.accountservice.entity.AccountResponseBody;

public class Serializer extends StdSerializer<AccountResponseBody> {

	public Serializer() {
		this(null);
	}

	protected Serializer(Class<AccountResponseBody> accountResponseBody) {
		super(accountResponseBody);
	}

	@Override
	public void serialize(AccountResponseBody accountResponseBody,
			JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		Map<String, List<Map<String, Object>>> nodeMap = new HashMap<String, List<Map<String, Object>>>();

		jsonGenerator.writeStartObject();

		for (Field field : accountResponseBody.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(JsonPath.class)) {
				JsonPath jsonPath = field.getAnnotation(JsonPath.class);

				Map<String, Map<String, Object>> fieldNodeMap = new HashMap<>();
				try {
					fieldNodeMap = buildNodeMap(jsonPath.path(),
							field.get(accountResponseBody));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				for (String key : fieldNodeMap.keySet()) {
					if (nodeMap.containsKey(key)) {
						List<Map<String, Object>> list = nodeMap.get(key);
						list.add(fieldNodeMap.get(key));
						nodeMap.put(key, list);
					} else {
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						list.add(fieldNodeMap.get(key));
						nodeMap.put(key, list);
					}
				}
			}
		}

		for (String path : nodeMap.keySet()) {
			JsonNode tempJson = generateJson(path, nodeMap.get(path));

		}

		jsonGenerator.writeEndObject();
	}

	public JsonNode generateJson(String path, List<Map<String, Object>> list)
			throws IOException {
		List<String> splittetPath = Splitter.on(".").splitToList(path);

		// Create the node factory that gives us nodes.
		JsonNodeFactory factory = new JsonNodeFactory(false);

		// create a json factory to write the treenode as json. for the example
		// we just write to console
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator generator = jsonFactory.createGenerator(System.out);
		ObjectMapper mapper = new ObjectMapper();

		// the root node - album
		JsonNode root = factory.objectNode();

		ObjectNode last = null;

		for (String node : splittetPath) {
			// ObjectNode newJsonNode = factory.objectNode();
			// newJsonNode.putObject(node);
			// ((ObjectNode) last).putAll(newJsonNode);
			// last = newJsonNode;
			if (last == null) {
				last = (ObjectNode) root;
			}
			last = ((ObjectNode) last).putObject(node);
		}

		for (Map<String, Object> nodeAttribute : list) {
			for (String key : nodeAttribute.keySet()) {
				((ObjectNode) root).put(key, (String) nodeAttribute.get(key));
			}
		}

		mapper.writeTree(generator, root);
		return null;
	}

	public Map<String, Map<String, Object>> buildNodeMap(String jsonPath,
			Object object) {

		List<String> splittetPath = Splitter.on(".").splitToList(jsonPath);

		Map<String, Map<String, Object>> pathMap = new HashMap<String, Map<String, Object>>();
		String lastEntry = splittetPath.get(splittetPath.size() - 1);
		String path = jsonPath.replace("." + lastEntry, "");

		if (pathMap.containsKey(path)) {
			Map<String, Object> nodeList = pathMap.get(path);
			nodeList.put(lastEntry, object);
		} else {
			Map<String, Object> nodeList = new HashMap<String, Object>();
			nodeList.put(lastEntry, object);
			pathMap.put(path, nodeList);
		}

		return pathMap;
	}

	public static void merge(JsonNode toBeMerged, JsonNode mergedInTo) {
		Iterator<Map.Entry<String, JsonNode>> incomingFieldsIterator = toBeMerged
				.fields();
		Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergedInTo
				.fields();

		while (incomingFieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> incomingEntry = incomingFieldsIterator
					.next();

			JsonNode subNode = incomingEntry.getValue();

			if (subNode.getNodeType().equals(JsonNodeType.OBJECT)) {
				boolean isNewBlock = true;
				mergedIterator = mergedInTo.fields();
				while (mergedIterator.hasNext()) {
					Map.Entry<String, JsonNode> entry = mergedIterator.next();
					if (entry.getKey().equals(incomingEntry.getKey())) {
						merge(incomingEntry.getValue(), entry.getValue());
						isNewBlock = false;
					}
				}
				if (isNewBlock) {
					((ObjectNode) mergedInTo).replace(incomingEntry.getKey(),
							incomingEntry.getValue());
				}
			} else if (subNode.getNodeType().equals(JsonNodeType.ARRAY)) {
				boolean newEntry = true;
				mergedIterator = mergedInTo.fields();
				while (mergedIterator.hasNext()) {
					Map.Entry<String, JsonNode> entry = mergedIterator.next();
					if (entry.getKey().equals(incomingEntry.getKey())) {
						updateArray(incomingEntry.getValue(), entry);
						newEntry = false;
					}
				}
				if (newEntry) {
					((ObjectNode) mergedInTo).replace(incomingEntry.getKey(),
							incomingEntry.getValue());
				}
			}
			ValueNode valueNode = null;
			JsonNode incomingValueNode = incomingEntry.getValue();
			switch (subNode.getNodeType()) {
			case STRING:
				valueNode = new TextNode(incomingValueNode.textValue());
				break;
			case NUMBER:
				valueNode = new IntNode(incomingValueNode.intValue());
				break;
			case BOOLEAN:
				valueNode = BooleanNode.valueOf(incomingValueNode
						.booleanValue());
			}
			if (valueNode != null) {
				updateObject(mergedInTo, valueNode, incomingEntry);
			}
		}
	}

	private static void updateArray(JsonNode valueToBePlaced,
			Map.Entry<String, JsonNode> toBeMerged) {
		toBeMerged.setValue(valueToBePlaced);
	}

	private static void updateObject(JsonNode mergeInTo,
			ValueNode valueToBePlaced, Map.Entry<String, JsonNode> toBeMerged) {
		boolean newEntry = true;
		Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergeInTo
				.fields();
		while (mergedIterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = mergedIterator.next();
			if (entry.getKey().equals(toBeMerged.getKey())) {
				newEntry = false;
				entry.setValue(valueToBePlaced);
			}
		}
		if (newEntry) {
			((ObjectNode) mergeInTo).replace(toBeMerged.getKey(),
					toBeMerged.getValue());
		}
	}

}
