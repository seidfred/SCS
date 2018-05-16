package de.seidfred.accountservice.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SerializerTest {

	@Test
	public void test() {
		Serializer underTest = new Serializer();
		
		String path = "ACC.STS.INF";
		
		Map<String, List<String>> result = underTest.buildNodeMap(path);
		
		assertNotNull(result);
	}

}
