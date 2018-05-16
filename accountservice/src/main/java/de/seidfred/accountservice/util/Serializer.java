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
			generateJson(path, nodeMap.get(path), jsonGenerator);
		}

		jsonGenerator.writeEndObject();
	}

	private void generateJson(String path, List<Map<String, Object>> list,
			JsonGenerator jsonGenerator) throws IOException {
		List<String> splittetPath = Splitter.on(".").splitToList(path);
		for (String node : splittetPath) {
			jsonGenerator.writeFieldName(node);
			jsonGenerator.writeStartObject();
		}

		for (Map<String, Object> nodeAttribute : list) {
			for (String key : nodeAttribute.keySet()) {
				jsonGenerator.writeStringField(key,
						(String) nodeAttribute.get(key));
			}
		}

		for (String node : splittetPath) {
			jsonGenerator.writeEndObject();
		}
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

}
