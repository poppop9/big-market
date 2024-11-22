package app.xlog.ggbond.persistent.po;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class LongListToJsonConverter implements AttributeConverter<List<Long>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 实体属性 --->>> 数据库列
     */
    @Override
    public String convertToDatabaseColumn(List<Long> longs) {
        try {
            return objectMapper.writeValueAsString(longs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSON", e);
        }
    }

    /**
     * 数据库列 --->>> 实体属性
     */
    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to list", e);
        }
    }

}
