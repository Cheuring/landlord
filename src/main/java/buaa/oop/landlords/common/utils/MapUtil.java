package buaa.oop.landlords.common.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    private final Map<String, Object> data;

    private MapUtil() {
        this.data = new HashMap<>();
    }

    public static MapUtil newInstance() {
        return new MapUtil();
    }


    /**
     * put a key-value pair into the map
     * if the value is a String, Number, Boolean or Character, put it directly
     * otherwise, convert it to json string
     * @param key   key
     * @param value value
     * @return this
     */
    public MapUtil put(String key, Object value) {
        if(value == null) {
            return this;
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character) {
            this.data.put(key, value);
        } else {
            this.data.put(key, JsonUtil.toJson(value));
        }
        return this;
    }


    /**
     * parse json string to map
     * complex type remains json string
     * you should call JsonUtil.fromJson to convert it to object
     * @param json json string
     * @return map
     */
    public static Map<String, Object> parse(String json) {
        if(json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        return JsonUtil.fromJson(json, Map.class);
    }

    public String json() {
        return JsonUtil.toJson(this.data);
    }

    public Map<String, Object> map() {
        return this.data;
    }
}
