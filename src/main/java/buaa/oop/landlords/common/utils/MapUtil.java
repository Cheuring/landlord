package buaa.oop.landlords.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapUtil {
    private final Map<String, Objects> data;

    private MapUtil() {
        this.data = new HashMap<>();
    }

    public static MapUtil newInstance() {
        return new MapUtil();
    }

    public MapUtil put(String key, Objects value) {
        this.data.put(key, value);
        return this;
    }

    public static Map<String, Objects> parse(String json) {
        return JsonUtil.fromJson(json, Map.class);
    }

    public String json() {
        return JsonUtil.toJson(this.data);
    }
}
