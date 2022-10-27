package com.uangel.ktiscp.nscp.common.json;

import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JsonTypeTest {
    JsonType loadJsonFile(String fileName) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            return JsonType.getJsonObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void basicLoadTest()
    {
        assertFalse( loadJsonFile("src/main/resources/test.json") == null);
    }

    @Test
    public void makeJsonStringTest() {
        JsonType loadJson = loadJsonFile("src/main/resources/test.json");
        JsonType makeJson = JsonType.makeJsonObject();

        makeJson.addObject("yun32s");
        makeJson.get("yun32s").setValue("name", "Yunku, Lee");
        makeJson.get("yun32s").setValue("age", 48);
        makeJson.addArray("track");
        makeJson.get("track").addValue("home");
        makeJson.get("track").addValue("bus terminal - home");
        makeJson.get("track").addValue("bus terminal - giheung");
        makeJson.get("track").addValue("subway station - giheung");
        makeJson.get("track").addValue("subway station - snne");
        makeJson.get("track").addValue("compay");
        makeJson.get("yun32s").addObject("school");
        makeJson.get("yun32s", "school").setValue("kindergarden", "pyengsung");
        makeJson.get("yun32s", "school").setValue("elementary-school", "myeonghak");
        makeJson.get("yun32s", "school").setValue("middle-school", "gwanyang");
        makeJson.get("yun32s", "school").setValue("high-school", "sinsung");
        makeJson.get("yun32s", "school").setValue("university", "kyonggi");
        makeJson.get("yun32s").addArray("test");
        makeJson.get("yun32s", "test").addArrayValue(2);
        makeJson.get("yun32s", "test").get(0).addValue("arr1-value1");
        makeJson.get("yun32s", "test").get(0).addValue("arr1-value2");
        makeJson.get("yun32s", "test").get(1).addValue("arr2-value1");
        makeJson.get("yun32s", "test").get(1).addValue("arr2-value2");

        assertFalse(loadJson.toString().equals(makeJson.toString()));
    }
}
