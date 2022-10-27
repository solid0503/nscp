package com.uangel.ktiscp.nscp.common.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonType {
    JSONObject jsonObject;
    JSONArray jsonArray;
    Object jsonValue;

    static public JsonType getJsonObject(String json) throws JSONException {
        return new JsonType(json);
    }

    static public JsonType makeJsonObject() {
        JsonType value = new JsonType();
        value.jsonObject = new JSONObject();
        return value;
    }

    public JsonType() {
    }
    public JsonType(JSONObject object) {
        jsonObject = object;
    }
    public JsonType(String json) throws JSONException {
        jsonObject = new JSONObject(json);
    }
    public JsonType(JSONArray array) {
        jsonArray = array;
    }
    public JsonType(Object value) {
        jsonValue = value;
    }

    private JsonType objectToJsonType(Object value) {
        if(value instanceof JSONObject) {
            return new JsonType((JSONObject)value);
        } else if(value instanceof JSONArray) {
            return new JsonType((JSONArray)value);
        } else {
            return new JsonType(value);
        }
    }
    public JsonType get(String attrName) {
        try {
            return objectToJsonType(jsonObject.get(attrName));
        } catch(Exception e) {
            return null;
        }
    }
    public JsonType get(String attrName, int index) {
        try {
            return objectToJsonType(jsonObject.getJSONArray(attrName).get(index));
        } catch (Exception e) {
            return null;
        }
    }
    public JsonType get(int index) {
        try {
            return objectToJsonType(jsonArray.get(index));
        } catch (Exception e) {
            return null;
        }
    }
    public JsonType get(String ... attrNames) {
        JsonType child = this;
        for(int i=0; i<attrNames.length; i++) {
            child = child.get(attrNames[i]);
            if(child == null) {
                return null;
            }
        }

        return child;
    }

    public JsonType addObject(String attrName) {
        try {
            JSONObject attr = new JSONObject();
            jsonObject.put(attrName, attr);
            return new JsonType(attr);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonType addArray(String attrName) {
        try {
            JSONArray array = new JSONArray();
            jsonObject.put(attrName, array);
            return new JsonType(array);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getValue(String attrName) {
        try {
            return jsonObject.get(attrName);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getValue(int index) {
        try {
            return jsonArray.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getValue(String attrName, int index) {
        try {
            return jsonObject.getJSONArray(attrName).get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setValue(String attrName, Object value) {
        try {
            jsonObject.put(attrName, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean addValue(Object value) {
        try {
            jsonArray.put(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean addValue(String attrName, Object value) {
        try {
            jsonObject.getJSONArray(attrName).put(value);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public JsonType addArrayValue() {
        return addArrayValue(1);
    }
    public JsonType addArrayValue(int length) {
        try {
            JSONArray array = null;
            for(int i=0; i<length; i++) {
                array = new JSONArray();
                jsonArray.put(array);
            }
            if(array != null) {
                return new JsonType(array);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        try {
            if(jsonObject != null) {
                return jsonObject.toString(3);
            } else if(jsonArray != null) {
                return jsonArray.toString(3);
            } else if(jsonValue != null) {
                return jsonValue.toString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}