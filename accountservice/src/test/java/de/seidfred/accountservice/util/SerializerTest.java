package de.seidfred.accountservice.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializerTest {

	@Test
	public void test() throws IOException {
		Serializer underTest = new Serializer();

		String path = "ACC.INF.ID";

		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();

		underTest.generateJson(path, "Test123");
	}

    @Test
    public void test_array() throws IOException {
        Serializer underTest = new Serializer();

        String path = "cstmrCdtTrfInitn.splmtryData[.envlp.ingprcgData.inggblRefs.inginstrRefs.inginstrId";

        List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();

        underTest.generateJson(path, "5b311df2-8664-395b-8dad-b4ac771206c7");
    }

	@Test
	public void test_merge() throws IOException {
		Serializer underTest = new Serializer();

		String path = "ACC.INF";
		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("ID", "12345");
		temp.add(tempMap);

		String pathB = "ACC.STS.INF";
		List<Map<String, Object>> tempB = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMapB = new HashMap<String, Object>();
		tempMapB.put("TXT", "Das ist eine Status Meldung");
		tempB.add(tempMapB);

		String pathC = "ACC.STS.RSN";
		List<Map<String, Object>> tempC = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMapC = new HashMap<String, Object>();
		tempMapC.put("ENUM", "ACSC");
		tempB.add(tempMapC);

		JsonNode jsonA = underTest.generateJson(path, temp);
		JsonNode jsonB = underTest.generateJson(pathB, tempB);
		JsonNode jsonC = underTest.generateJson(pathB, tempC);

		underTest.merge(jsonA, jsonB);

		underTest.merge(jsonB, jsonC);

		System.out.println("\n\n\n\n");

		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator generator = jsonFactory.createGenerator(System.out);
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeTree(generator, jsonC);

	}
}
