package com.flower.game.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static String toString(Object object) {
        ObjectMapper om = SpringContextHolder.getBean(ObjectMapper.class);
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> T readValue(String text, Class<T> clazz) {
        ObjectMapper om = SpringContextHolder.getBean(ObjectMapper.class);
        try {
            return om.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
