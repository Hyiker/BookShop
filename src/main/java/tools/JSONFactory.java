package tools;

import gui.JSONType;

import java.util.ArrayList;
import java.util.TreeMap;

public class JSONFactory {


static class JSONProxyObject {
    private final JSONObject json;
    private String key = null;
    private final boolean isArray;
    private boolean keyTurn = true;

    public JSONProxyObject(boolean isMap) {
        json = new JSONObject();
        if (isMap) {
            json.type = JSONType.MAP;
            json.map = new TreeMap<>();
        } else {
            json.type = JSONType.ARRAY;
            json.array = new ArrayList<>();
        }
        this.isArray = !isMap;
    }

    private void key(String k) {
        if (isArray) {
            throw new IllegalArgumentException("Cannot set key on an array");
        }
        this.key = k;
    }

    private void value(JSONObject value) {
        if (isArray) {
            json.array.add(value);
        } else {
            json.map.put(key, value);
            this.key = null;
        }
    }

    public void next(JSONObject value) {
        if (!isArray && keyTurn) {
            if (value.type == JSONType.STRING) {
                key(value.string);
            } else {
                throw new IllegalArgumentException("Key must be a string");
            }
        } else {
            this.value(value);
        }
        keyTurn = !keyTurn;
    }

    public JSONObject build() {
        if (this.key != null) {
            throw new IllegalArgumentException("JSONProxyObject is not complete");
        }
        return json;
    }
}

    // singleton
    private static final JSONFactory instance = new JSONFactory();

    public static JSONFactory getInstance() {
        return instance;
    }

    public JSONProxyObject buildCascadeJSON(char bracket) {
        return new JSONProxyObject(bracket == '{');
    }

    public JSONObject buildNull() {
        return null;
    }

    public JSONObject buildBool(String text) {
        JSONObject json = new JSONObject();
        json.type = JSONType.BOOLEAN;
        json.booleanValue = "true".equals(text);
        return json;
    }

    public JSONObject buildString(String identifier) {
        JSONObject json = new JSONObject();
        json.type = JSONType.STRING;
        json.string = identifier.substring(1, identifier.length() - 1);
        return json;
    }

    public JSONObject buildFloat(String identifier) {
        JSONObject json = new JSONObject();
        json.type = JSONType.FLOAT;
        json.floatValue = Float.parseFloat(identifier);
        return json;
    }

    public JSONObject buildInteger(String identifier) {
        JSONObject json = new JSONObject();
        json.type = JSONType.INT;
        json.intValue = Integer.parseInt(identifier);
        return json;
    }


}
