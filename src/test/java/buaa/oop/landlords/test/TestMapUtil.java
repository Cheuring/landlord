package buaa.oop.landlords.test;

import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMapUtil implements Serializable{

    @Test
    public void testMapUtil() {
        List<TestClass> testClasses = new ArrayList<>();
        testClasses.add(new TestClass("name1", 10));
        testClasses.add(new TestClass("name2", 20));

        String json = MapUtil.newInstance()
                .put("key1", "value")
                .put("key2", 10)
                .put("key5", false)
                .put("key6", 'a')
                .put("key3", new TestClass("name", 10))
                .put("key4", testClasses)
                .json();

        System.out.println(json);

        Map<String, Object> map = MapUtil.parse(json);
        System.out.println(map);

//        TestClass key3 = JsonUtil.fromJson((String) map.get("key3"), TestClass.class);
//        System.out.println(key3);

        System.out.println(map.get("key1").getClass());
        System.out.println(map.get("key2").getClass());
        System.out.println(map.get("key5").getClass());
        System.out.println(map.get("key6").getClass());
        System.out.println(map.get("key3"));
        System.out.println(map.get("key4"));
//        int a = ((Double) map.get("key2")).intValue();
//        System.out.println(a);

    }

    @Test
    public void testMapUtil2() {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        // Create a map with different types of values
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value");
        map.put("key2", 10);
        map.put("key3", new TestClass("name", 10));

        // Serialize the map to JSON
        String json = gson.toJson(map);
        System.out.println("Serialized JSON: " + json);

        // Deserialize the JSON back to a map
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> deserializedMap = gson.fromJson(json, type);
        System.out.println("Deserialized Map: " + deserializedMap);

        System.out.println(deserializedMap.get("key1").getClass());
        System.out.println(deserializedMap.get("key2").getClass());
        System.out.println(deserializedMap.get("key3").getClass());
    }

    @Test
    public void testMapUtil3() {
        List<TestClass> testClasses = new ArrayList<>();
        testClasses.add(new TestClass("name1", 10));
        testClasses.add(new TestClass("name2", 20));

        String json = MapUtil.newInstance()
                .put("key1", "value")
                .put("key2", 10)
                .put("key3", new TestClass("name", 10))
                .put("key4", testClasses)
                .json();

        Map<String, Object> map = MapUtil.parse(json);
        System.out.println(map);

        TestClass key3 = JsonUtil.fromJson((String) map.get("key3"), TestClass.class);
        System.out.println(key3);

//        Type listType = new TypeToken<ArrayList<TestClass>>() {}.getType();
//        List<?> key4 = JsonUtil.fromJson((String) map.get("key4"), listType);
        ArrayList<TestClass> key4 = JsonUtil.fromJson((String) map.get("key4"), new TypeReference<ArrayList<TestClass>>() {
        });
        System.out.println(key4);
    }

}
@AllArgsConstructor
@NoArgsConstructor
@Data
class TestClass {
    private String name;
    private int age;
}
