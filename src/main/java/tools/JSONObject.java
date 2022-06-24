package tools;

import gui.JSONType;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;


public class JSONObject implements Serializable {
    protected JSONType type;
    TreeMap<String, JSONObject> map;
    ArrayList<JSONObject> array;
    String string;
    int intValue;
    float floatValue;
    boolean booleanValue;

    public boolean isContainer() {
        return type == JSONType.MAP || type == JSONType.ARRAY;
    }

    public JSONType getType() {
        return type;
    }

    public int getIntValue() {
        if (type != JSONType.INT) {
            throw new InvalidJSONAccessException("JSONObject is not an int");
        }
        return intValue;
    }

    public float getFloatValue() {
        if (type != JSONType.FLOAT) {
            throw new InvalidJSONAccessException("JSONObject is not a float");
        }
        return floatValue;
    }

    public boolean getBooleanValue() {
        if (type != JSONType.BOOLEAN) {
            throw new InvalidJSONAccessException("JSONObject is not a boolean");
        }
        return booleanValue;
    }

    public String getStringValue() {
        if (type != JSONType.STRING) {
            throw new InvalidJSONAccessException("JSONObject is not a string");
        }
        return string;
    }

    public Object getValue() {
        if (type == JSONType.INT) {
            return intValue;
        } else if (type == JSONType.FLOAT) {
            return floatValue;
        } else if (type == JSONType.BOOLEAN) {
            return booleanValue;
        } else if (type == JSONType.STRING) {
            return string;
        } else {
            throw new InvalidJSONAccessException("JSONObject is a container");
        }
    }

    public ArrayList<String> getKeys() {
        if (type != JSONType.MAP) {
            throw new InvalidJSONAccessException("JSONObject is not a map");
        }
        return new ArrayList<>(map.keySet());
    }

    public JSONObject get(String key) {
        if (type != JSONType.MAP) {
            throw new InvalidJSONAccessException("JSONObject is not a map");
        }
        return map.get(key);
    }

    public boolean hasKey(String key) {
        if (type != JSONType.MAP) {
            throw new InvalidJSONAccessException("JSONObject is not a map");
        }
        return map.containsKey(key);
    }

    public int size() {
        if (!isContainer()) {
            throw new InvalidJSONAccessException("JSONObject is not an array");
        }
        if (type == JSONType.MAP) {
            return map.size();
        } else {
            return array.size();
        }
    }

    public JSONObject get(int index) {
        if (type != JSONType.ARRAY) {
            throw new InvalidJSONAccessException("JSONObject is not an array");
        }
        return array.get(index);
    }

    public <T> T cast(Class<T> clazz) throws JSONReflectException {
        T obj;
        try {
            Constructor<T> constructor = clazz.getConstructor();
            obj = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new JSONReflectException("Failed to create instance of class " + clazz.getName());
        }

        if (this.type != JSONType.MAP) {
            throw new JSONReflectException("JSONObject is not a map");
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (this.hasKey(fieldName)) {
                try {
                    field.setAccessible(true);
                    field.set(obj, this.get(fieldName).getValue());
                } catch (IllegalAccessException e) {
                    throw new JSONReflectException("Failed to set field " + fieldName);
                }
            }
        }
        return obj;
    }

    @Override
    public String toString() {
        if (type == JSONType.MAP) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (String key : map.keySet()) {
                sb.append("\"").append(key).append("\":").append(map.get(key)).append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("}");
            return sb.toString();
        } else if (type == JSONType.ARRAY) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (JSONObject obj : array) {
                sb.append(obj.toString()).append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            return sb.toString();
        } else if (type == JSONType.STRING) {
            return "\"" + string + "\"";
        } else if (type == JSONType.INT) {
            return Integer.toString(intValue);
        } else if (type == JSONType.FLOAT) {
            return Float.toString(floatValue);
        } else if (type == JSONType.BOOLEAN) {
            return Boolean.toString(booleanValue);
        } else {
            return "null";
        }
    }
}
