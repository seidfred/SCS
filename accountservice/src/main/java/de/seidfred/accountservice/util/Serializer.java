package de.seidfred.accountservice.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
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
		Map<String, List<String>> nodeMap = new HashMap<String, List<String>>();

		jsonGenerator.writeStartObject();

		for (Field field : accountResponseBody.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(JsonPath.class)) {
				JsonPath jsonPath = field.getAnnotation(JsonPath.class);

				List<String> splittetPath = Splitter.on(".").splitToList(
						jsonPath.path());

				Map<String, List<String>> fieldNodeMap = buildNodeMap(
						jsonPath.path(), splittetPath);

				for (String key : fieldNodeMap.keySet()) {
					if (nodeMap.containsKey(key)) {
						List<String> list = nodeMap.get(key);
						list.addAll(fieldNodeMap.get(key));
						nodeMap.put(key, list);
					} else {
						nodeMap.put(key, fieldNodeMap.get(key));
					}
				}
			}
		}

		for (String path : nodeMap.keySet()) {
			generateJson(path, nodeMap.get(path), jsonGenerator);
		}

		jsonGenerator.writeEndObject();
	}

	private void generateJson(String path, List<String> nodeAttributes,
			JsonGenerator jsonGenerator) throws IOException {
		List<String> splittetPath = Splitter.on(".").splitToList(path);

		for (String node : splittetPath) {
			jsonGenerator.writeFieldName(node);
			jsonGenerator.writeStartObject();
		}

		for (String nodeAttribute : nodeAttributes) {
			jsonGenerator.writeStringField(nodeAttribute, "blub");
		}

		for (String node : splittetPath) {
			jsonGenerator.writeEndObject();
		}
	}

	private Map<String, List<String>> buildNodeMap(String jsonPath,
			List<String> splittetPath) {
		Map<String, List<String>> pathMap = new HashMap<String, List<String>>();
		String lastEntry = splittetPath.get(splittetPath.size() - 1);
		String path = jsonPath.replace("." + lastEntry, "");

		if (pathMap.containsKey(path)) {
			List<String> nodeList = pathMap.get(path);
			nodeList.add(lastEntry);
		} else {
			List<String> nodeList = new ArrayList<String>();
			nodeList.add(lastEntry);
			pathMap.put(path, nodeList);
		}

		return pathMap;
	}

}
