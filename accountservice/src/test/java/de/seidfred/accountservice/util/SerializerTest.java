package de.seidfred.accountservice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SerializerTest {

	@Test
	public void test() throws IOException {
		Serializer underTest = new Serializer();

		String path = "ACC.INF";

		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();

		Map<String, Object> tempMap = new HashMap<String, Object>();

		tempMap.put("ID", "12345");

		temp.add(tempMap);

		underTest.generateJson(path, temp);
	}

}
