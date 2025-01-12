package app.xlog.ggbond.persistent.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.LinkedHashMap;

@Converter
public class MapToJsonConverter implements AttributeConverter<LinkedHashMap<Long, Long>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 Map 转换为 JSON 字符串
     */
    @Override
    public String convertToDatabaseColumn(LinkedHashMap<Long, Long> map) {
        if (map == null) return null;

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Map to JSON", e);
        }
    }

    /**
     * 将 JSON 字符串转换为 Map
     */
    @Override
    public LinkedHashMap<Long, Long> convertToEntityAttribute(String json) {
        if (StrUtil.isBlank(json)) return null;

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to Map", e);
        }
    }

}
