package de.seidfred.accountservice.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Splitter;

import de.seidfred.accountservice.entity.AccountRequestBody;

public class Deserializer extends StdDeserializer<AccountRequestBody> {
	private static final long serialVersionUID = 1L;

	public Deserializer() {
		this(null);
	}

	public Deserializer(Class<AccountRequestBody> accountRequestBody) {
		super(accountRequestBody);
	}

	@Override
	public AccountRequestBody deserialize(JsonParser jsonParser,
			DeserializationContext context) throws IOException,
			JsonProcessingException {
		JsonNode jsonTree = jsonParser.getCodec().readTree(jsonParser);

		BeanWrapper wrappedAccountReqeustBody = new BeanWrapperImpl(
				new AccountRequestBody());
		Class<AccountRequestBody> clz = AccountRequestBody.class;

		for (Field field : clz.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(JsonPath.class)) {
				JsonPath jsonPath = field.getAnnotation(JsonPath.class);

				System.out.println(field.getName() + " - " + field.getType()
						+ " - " + jsonPath.path());

				List<String> splittetPath = Splitter.on(".").splitToList(
						jsonPath.path());
				Object value = getValue(jsonTree, splittetPath);
				if (field.getType().isAssignableFrom(Date.class)) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date = null;
					try {
						date = format.parse((String) value);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					wrappedAccountReqeustBody.setPropertyValue(field.getName(),
							date);
				} else {
					wrappedAccountReqeustBody.setPropertyValue(field.getName(),
							value);
				}
			}
		}

		return (AccountRequestBody) wrappedAccountReqeustBody
				.getWrappedInstance();
	}

	private Object getValue(JsonNode root, List<String> splittetPath) {
		JsonNode node = root;
		for (String nodeName : splittetPath) {
			node = getNode(node, nodeName);
		}

		return node.textValue();
	}

	private JsonNode getNode(JsonNode root, String nodeName) {
		JsonNode node = null;
		if (isJsonArray(nodeName)) {
			node = root.get(0).get(nodeName);
		} else {
			node = root.get(nodeName);
			if (node == null) {
				throw new IllegalArgumentException();
			}
		}
		return node;
	}

	private boolean isJsonArray(String nodeName) {
		return nodeName.contains("[]");
	}
}
