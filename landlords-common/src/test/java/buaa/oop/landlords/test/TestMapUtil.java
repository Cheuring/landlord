package buaa.oop.landlords.test;

import buaa.oop.landlords.common.utils.JsonUtil;
import buaa.oop.landlords.common.utils.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
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

//    @Test
//    public void testMapUtil2() {
//        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
//
//        // Create a map with different types of values
//        Map<String, Object> map = new HashMap<>();
//        map.put("key1", "value");
//        map.put("key2", 10);
//        map.put("key3", new TestClass("name", 10));
//
//        // Serialize the map to JSON
//        String json = gson.toJson(map);
//        System.out.println("Serialized JSON: " + json);
//
//        // Deserialize the JSON back to a map
//        Type type = new TypeToken<Map<String, Object>>() {}.getType();
//        Map<String, Object> deserializedMap = gson.fromJson(json, type);
//        System.out.println("Deserialized Map: " + deserializedMap);
//
//        System.out.println(deserializedMap.get("key1").getClass());
//        System.out.println(deserializedMap.get("key2").getClass());
//        System.out.println(deserializedMap.get("key3").getClass());
//    }

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

    /**
     * test SHOW_ROOMS JSON format
     */
    @Test
    public void testMapUtil4() {
        List<Map<String, Object>> roomList = new ArrayList<>();
        roomList.add(MapUtil.newInstance()
                .put("roomId", 1)
                .put("roomOwner", "owner1")
                .put("roomClientCount", 2)
                .put("roomType", "type1")
                .map());
        roomList.add(MapUtil.newInstance()
                .put("roomId", 2)
                .put("roomOwner", "owner2")
                .put("roomClientCount", 3)
                .put("roomType", "type2")
                .map());

        String json = JsonUtil.toJson(roomList);
        System.out.println(json);

        List<Map<String, Object>> roomList2 = JsonUtil.fromJson(json, new TypeReference<List<Map<String, Object>>>() {
        });

        for (Map<String, Object> room : roomList2) {
            System.out.println(room.get("roomId"));
            System.out.println(room.get("roomOwner"));
            System.out.println(room.get("roomClientCount"));
            System.out.println(room.get("roomType"));
        }
    }

    @Test
    public void testMapUtil5(){
        String json = MapUtil.newInstance()
                .put("key1", null)
                .put("key2", 2)
                .put("key3", "hello")
                .json();

        Map<String, Object> map = MapUtil.parse(json);
        System.out.println(map.get("key1") == null);
    }

    @Test
    public void testMapUtil6(){
        Map<String, Object> map = MapUtil.parse("");
        System.out.println(map);
        Map<String, Object> map1 = MapUtil.parse(null);
        System.out.println(map1);
    }

}
@AllArgsConstructor
@NoArgsConstructor
@Data
class TestClass {
    private String name;
    private int age;
}
