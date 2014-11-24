package mixedserver.protocol.jsonrpc.client;

import java.io.IOException;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.saintangelo.application.User;

public class DtoDiffrent extends TestCase {

	private static ObjectMapper objectmapper = new ObjectMapper();

	public void testOpenProxy() throws JsonParseException,
			JsonMappingException, IOException {

		// 参数不够的时候
		User u = objectmapper.readValue("{ \"id\": 3, \"name\" :\"jack\" }",
				User.class);

		assertEquals("jack", u.getName());
		assertEquals(3, u.getId());
		assertNull(u.getPhone());
		assertNull(u.getEmail());

		// 参数刚好的时候
		u = objectmapper
				.readValue(
						"{ \"id\": 3, \"name\" :\"jack\", \"email\": \"jack@gmail.com\", \"phone\": \"18658730118\" }",
						User.class);

		assertEquals("jack", u.getName());
		assertEquals(3, u.getId());
		assertEquals("18658730118", u.getPhone());
		assertEquals("jack@gmail.com", u.getEmail());

		// 参数多出来的时候
		objectmapper
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		u = objectmapper
				.readValue(
						"{ \"id\": 3, \"name\" :\"jack\", \"email\": \"jack@gmail.com\", \"phone\": \"18658730118\""
								+ ", \"someOther\": \"foobar\" }", User.class);

		assertEquals("jack", u.getName());
		assertEquals(3, u.getId());
		assertEquals("18658730118", u.getPhone());
		assertEquals("jack@gmail.com", u.getEmail());

		// 参数有多有少的时候
		objectmapper
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		u = objectmapper.readValue("{ \"id\": 3, \"phone\": \"18658730118\""
				+ ", \"someOther\": \"foobar\" }", User.class);

		assertNull(u.getName());
		assertNull(u.getEmail());
		assertEquals(3, u.getId());
		assertEquals("18658730118", u.getPhone());

	}
}
