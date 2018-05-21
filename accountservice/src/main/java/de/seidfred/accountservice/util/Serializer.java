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

		Map<String, Object> fieldNodeMap = new HashMap<String, Object>();

		for (Field field : accountResponseBody.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(JsonPath.class)) {
				JsonPath jsonPath = field.getAnnotation(JsonPath.class);

				try {
					if (fieldNodeMap.containsKey(jsonPath)) {
						throw new IllegalArgumentException();
					} else {
						fieldNodeMap.put(jsonPath.path(),
								field.get(accountResponseBody));
					}

				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		List<JsonNode> jsonNodes = new ArrayList<JsonNode>();

		for (String path : fieldNodeMap.keySet()) {
			jsonNodes.add(generateJson(path, fieldNodeMap.get(path)));
		}

		Iterator<JsonNode> jsonNodeIterator = jsonNodes.iterator();

		JsonNode firstJsonNode = (JsonNode) jsonNodeIterator.next();

		while (jsonNodeIterator.hasNext()) {
			merge(jsonNodeIterator.next(), firstJsonNode);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeTree(jsonGenerator, firstJsonNode);
	}

	public JsonNode generateJson(String path, Object object) throws IOException {
		List<String> splittetPath = new ArrayList<String>();
		splittetPath.addAll(Splitter.on(".").splitToList(path));
		String lastEntry = splittetPath.remove(splittetPath.size() - 1);

		// Create the node factory that gives us nodes.
		JsonNodeFactory factory = new JsonNodeFactory(false);

		// create a json factory to write the treenode as json. for the example
		// we just write to console
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator generator = jsonFactory.createGenerator(System.out);
		ObjectMapper mapper = new ObjectMapper();

		JsonNode root = factory.objectNode();

		ObjectNode last = null;

		for (String node : splittetPath) {
			if (last == null) {
				last = (ObjectNode) root;
			}
			last = last.putObject(node);
		}

		if (object == null) {
			last.put(lastEntry, "");
		} else {
			last.put(lastEntry, object.toString());
		}

		mapper.writeTree(generator, root);
		return root;
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
