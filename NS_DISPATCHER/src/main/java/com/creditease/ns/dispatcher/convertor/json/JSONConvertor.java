package com.creditease.ns.dispatcher.convertor.json;


import com.creditease.ns.log.Log;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JSONConvertor {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJSON(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.logError("Object TO JSON:{} 出现异常:{}", e);
            return "";
        }
    }

    public static <T> T toObject(final String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            Log.logError("JSON TO OBJECT :" + json + " 出现异常:{}", e);
            return null;
        }
    }

    public static Map<String, String> jsonToMap(final String json) {
        try {
            final Map<String, Object> input = objectMapper.readValue(json,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            final Map<String, String> output = new HashMap<>(input.size());
            final StringWriter writer = new StringWriter();
            final StringBuffer buf = writer.getBuffer();
            for (final Map.Entry<String, Object> entry : input.entrySet()) {
                try (final JsonGenerator gen = new JsonFactory(objectMapper).createGenerator(writer)) {
                    gen.writeObject(entry.getValue());
                }
                output.put(entry.getKey(),  StringUtils.stripEnd(StringUtils.stripStart(buf.toString(), "\""),"\""));
                buf.setLength(0);
            }
            return output;
        } catch (IOException e) {
            Log.logError("JSON TO MAP :" + json + " 出现异常:{}", e);
            return null;
        }
    }

    public static boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = objectMapper.getFactory()
                    .createParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (JsonParseException jpe) {
            //do nothing
        } catch (IOException ioe) {
            //do nothing
        }

        return valid;
    }


}
