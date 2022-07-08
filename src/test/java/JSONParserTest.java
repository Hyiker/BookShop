import org.junit.Test;
import tools.BadTokenException;
import tools.JSONObject;
import tools.JSONParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class JSONParserTest {
    @Test
    public void testSimpleJSON() throws BadTokenException {
        JSONParser parser = new JSONParser();
        JSONObject object = parser.deserialize("{\"boolean\":true,\"color\":\"gold\",\"null\":null,\"number\":123,\"string\":\"Hello World\"}");

        System.out.println(object);

        assertTrue(object.get("boolean").getBooleanValue());
        assertEquals("gold", object.get("color").getStringValue());
        assertNull(object.get("null"));
        assertEquals(123, object.get("number").getIntValue());
        assertEquals("Hello World", object.get("string").getStringValue());
    }

    @Test
    public void testCascadeJson() throws BadTokenException {
        JSONParser parser = new JSONParser();
        JSONObject object = parser.deserialize("{\"boolean\":true,\"color\":\"gold\",\"null\":null,\"number\":123,\"string\":\"Hello World\",\"array\":[1,2,3,4,5],\"map\":{\"a\":1,\"b\":2,\"c\":3}}");

        System.out.println(object);

        assertTrue(object.get("boolean").getBooleanValue());
        assertEquals("gold", object.get("color").getStringValue());
        assertNull(object.get("null"));
        assertEquals(123, object.get("number").getIntValue());
        assertEquals("Hello World", object.get("string").getStringValue());
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, object.get("array").get(i).getIntValue());
        }
        assertEquals(1, object.get("map").get("a").getIntValue());
        assertEquals(2, object.get("map").get("b").getIntValue());
        assertEquals(3, object.get("map").get("c").getIntValue());
    }

    @Test
    public void testMultiLevelCascadeJson() throws BadTokenException {
        JSONParser parser = new JSONParser();
        JSONObject object = parser.deserialize("{\"array\":[1,2,{\"array\":[1,3],\"a\":1,\"b\":2,\"c\":3}],\"map\":{\"arr\":[\"arr\",123,true],\"a\":1,\"b\":2,\"c\":3}}");

        System.out.println(object);

        assertEquals(1, object.get("map").get("a").getIntValue());
        assertEquals(2, object.get("map").get("b").getIntValue());
        assertEquals(3, object.get("map").get("c").getIntValue());
        assertEquals("arr", object.get("map").get("arr").get(0).getStringValue());
        assertEquals(123, object.get("map").get("arr").get(1).getIntValue());
        assertTrue(object.get("map").get("arr").get(2).getBooleanValue());
        for (int i = 0; i < 2; i++) {
            assertEquals(i + 1, object.get("array").get(i).getIntValue());
        }
        assertEquals(1, object.get("array").get(2).get("a").getIntValue());
        assertEquals(2, object.get("array").get(2).get("b").getIntValue());
        assertEquals(3, object.get("array").get(2).get("c").getIntValue());
        assertEquals(1, object.get("array").get(2).get("array").get(0).getIntValue());
        assertEquals(3, object.get("array").get(2).get("array").get(1).getIntValue());
    }

    @Test
    public void testTopLevelArrayJson() throws BadTokenException {
        JSONParser parser = new JSONParser();
        JSONObject object = parser.deserialize("[\n" +
                "  1,\n" +
                "  2,\n" +
                "  {\n" +
                "    \"a\": 12\n" +
                "  },\n" +
                "  [\n" +
                "    1,\n" +
                "    2\n" +
                "  ],\n" +
                "  114514\n" +
                "]");
        System.out.println(object);
        assertEquals(1, object.get(0).getIntValue());
        assertEquals(2, object.get(1).getIntValue());
        assertEquals(12, object.get(2).get("a").getIntValue());
        assertEquals(1, object.get(3).get(0).getIntValue());
        assertEquals(2, object.get(3).get(1).getIntValue());
        assertEquals(114514, object.get(4).getIntValue());

    }

    @Test
    public void testStreamingDeserialize() throws BadTokenException {
        JSONParser parser = new JSONParser();
        String jsonStream = "[\n" +
                "  1,\n" +
                "  2,\n" +
                "  {\n" +
                "    \"a\": 12\n" +
                "  },\n" +
                "  [\n" +
                "    1,\n" +
                "    2\n" +
                "  ],\n" +
                "  114514\n" +
                "]";
        InputStream stream = new ByteArrayInputStream(jsonStream.getBytes());
        JSONObject object = parser.deserialize(stream);
        System.out.println(object);
        assertEquals(1, object.get(0).getIntValue());
        assertEquals(2, object.get(1).getIntValue());
        assertEquals(12, object.get(2).get("a").getIntValue());
        assertEquals(1, object.get(3).get(0).getIntValue());
        assertEquals(2, object.get(3).get(1).getIntValue());
        assertEquals(114514, object.get(4).getIntValue());

    }

    @Test
    public void testStreamingMultiLevelCascadeJson() throws BadTokenException {
        JSONParser parser = new JSONParser();
        String jsonStream = "{\"array\":[1,2,{\"array\":[1,3],\"a\":1,\"b\":2,\"c\":3}],\"map\":{\"arr\":[\"arr\",123,true],\"a\":1,\"b\":2,\"c\":3}}";
        InputStream stream = new ByteArrayInputStream(jsonStream.getBytes());
        JSONObject object = parser.deserialize(stream);
        System.out.println(object);
        assertEquals(1, object.get("map").get("a").getIntValue());
        assertEquals(2, object.get("map").get("b").getIntValue());
        assertEquals(3, object.get("map").get("c").getIntValue());
        assertEquals("arr", object.get("map").get("arr").get(0).getStringValue());
        assertEquals(123, object.get("map").get("arr").get(1).getIntValue());
        assertTrue(object.get("map").get("arr").get(2).getBooleanValue());
        for (int i = 0; i < 2; i++) {
            assertEquals(i + 1, object.get("array").get(i).getIntValue());
        }
        assertEquals(1, object.get("array").get(2).get("a").getIntValue());
        assertEquals(2, object.get("array").get(2).get("b").getIntValue());
        assertEquals(3, object.get("array").get(2).get("c").getIntValue());
        assertEquals(1, object.get("array").get(2).get("array").get(0).getIntValue());
        assertEquals(3, object.get("array").get(2).get("array").get(1).getIntValue());
    }
}
