package de.seidfred.accountservice.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
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
		MyTreeNode<Object> tree = null;

		jsonGenerator.writeStartObject();

		for (Field field : accountResponseBody.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(JsonPath.class)) {
				JsonPath jsonPath = field.getAnnotation(JsonPath.class);

				List<String> splittetPath = Splitter.on(".").splitToList(
						jsonPath.path());

				Object fieldValue = field.get(field.getType());
				tree = buildNodeMap(tree, splittetPath, fieldValue);

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

	private MyTreeNode<Object> buildNodeMap(MyTreeNode<Object> tree,
			List<String> splittetPath, Object fieldValue) {
			
		for (String pathStep : splittetPath) {
			if (tree == null) {
				tree = new MyTreeNode<Object>(pathStep);
			} else
			{
				tree.addChild(pathStep);
			}
		}

		return tree;
	}

}
