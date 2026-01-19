package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void objectToString() {
        TestObj testObj = new TestObj();
        testObj.setId("1");
        testObj.setName("test");
        testObj.setGender("0");
        System.out.println(JsonUtils.ObjectToString(testObj));
    }
//{"id":"1","name":"test","gender":"0"}
    @Test
    void objectToStringPretty() {
        TestObj testObj = new TestObj();
        testObj.setId("1");
        testObj.setName("test");
        testObj.setGender("0");
        System.out.println(JsonUtils.ObjectToStringPretty(testObj));
    }
//    {
//        "id" : "1",
//            "name" : "test",
//            "gender" : "0"
//    }
    @Test
    void stringToObject() {
        String json = "{\"id\":\"1\",\"name\":\"test\",\"gender\":\"0\"}";
        TestObj testObj = JsonUtils.StringToObject(json, TestObj.class);
        System.out.println(testObj);
    }
    //TestObj(id=1, name=test, gender=0)
}