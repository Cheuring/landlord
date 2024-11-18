package buaa.oop.landlords.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * convert json string to object
     * make sure the class has a no-args constructor
     * @param json  json string
     * @param clazz class of object
     * @return object
     * @param <T> type of object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if(json == null){
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * convert json string to object
     * make sure the class has a no-args constructor
     * @param json  json string
     * @param type class of object
     * @return object
     * @param <T> type of object
     */
    public static <T> T fromJson(String json, TypeReference<T> type) {
        if(json == null){
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
