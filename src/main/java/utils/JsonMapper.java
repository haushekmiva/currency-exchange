package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ReadFromJsonException;
import exceptions.WriteToJsonException;

public final class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonMapper() {
    }

    public <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new WriteToJsonException("Failed to serialize DTO into JSON response.", e);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new ReadFromJsonException("Failed to deserialize JSON into DTO.", e);
        }
    }
}
