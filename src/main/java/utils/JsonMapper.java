package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ReadFromJsonException;
import exceptions.WriteToJsonException;

import java.util.List;

public final class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonMapper() {
    }

    public static <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new WriteToJsonException("Failed to serialize DTO into JSON response.", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new ReadFromJsonException("Failed to deserialize JSON into DTO.", e);
        }
    }

    // понять как работает я хз
    public static <T> List<T> fromJsonList(String json, Class<T> elementClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // TypeFactory помогает Jackson понять что это List<T>
            JavaType type = mapper.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON list", e);
        }
    }
}


