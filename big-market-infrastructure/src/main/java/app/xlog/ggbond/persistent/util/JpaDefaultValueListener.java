package app.xlog.ggbond.persistent.util;

import jakarta.persistence.PrePersist;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JpaDefaultValueListener {

    @PrePersist
    @SneakyThrows
    public void prePersist(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            JpaDefaultValue annotation = field.getAnnotation(JpaDefaultValue.class);
            if (annotation != null) {
                field.setAccessible(true);
                Object value = field.get(entity);

                if (value == null) {
                    String howToCreate = annotation.howToCreate();

                    // 分离方法路径和参数部分
                    int paramStartIndex = howToCreate.indexOf('(');
                    int paramEndIndex = howToCreate.lastIndexOf(')');

                    if (paramStartIndex == -1 || paramEndIndex == -1) {
                        throw new IllegalArgumentException("howToCreate 格式错误，缺少括号");
                    }

                    // 提取方法路径和参数
                    String methodPath = howToCreate.substring(0, paramStartIndex).trim();
                    String paramString = howToCreate.substring(paramStartIndex + 1, paramEndIndex).trim();

                    // 分离类名和方法名
                    String[] parts = methodPath.split("\\.");
                    String className = String.join(".", Arrays.copyOf(parts, parts.length - 1));
                    String methodName = parts[parts.length - 1];

                    // 解析参数
                    Object[] params = parseParameters(paramString);
                    Class<?>[] paramTypes = Arrays.stream(params).map(this::getPrimitiveType).toArray(Class<?>[]::new);

                    // 获取类和方法
                    Class<?> clazz = Class.forName(className);
                    Method method = clazz.getDeclaredMethod(methodName, paramTypes);

                    // 调用方法生成值
                    Object generatedValue = method.invoke(null, params);

                    // 检查生成值的类型是否匹配
                    if (field.getType().isAssignableFrom(generatedValue.getClass())) {
                        field.set(entity, generatedValue);
                    } else {
                        throw new RuntimeException("生成器类型与属性类型不匹配");
                    }
                }
            }
        }
    }

    /**
     * 解析参数字符串为参数数组
     *
     * @param paramString 参数字符串（例如 "2000, 12, 31, 0, 0, 0"）
     * @return 转换后的参数数组
     */
    private Object[] parseParameters(String paramString) {
        if (paramString.isEmpty()) {
            return new Object[0];
        }

        // 简单实现：根据逗号分隔并尝试解析常见类型（int, long, double, String 等）
        String[] parts = paramString.split(",");
        Object[] params = new Object[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            // 尝试解析为数字
            if (part.matches("-?\\d+")) {
                params[i] = Integer.parseInt(part);
            } else if (part.matches("-?\\d+L")) {
                params[i] = Long.parseLong(part.substring(0, part.length() - 1));
            } else if (part.matches("-?\\d+\\.\\d+")) {
                params[i] = Double.parseDouble(part);
            } else if (part.matches("-?\\d+\\.\\d+f")) {
                params[i] = Float.parseFloat(part.substring(0, part.length() - 1));
            } else if (part.startsWith("\"") && part.endsWith("\"")) {
                // 解析为字符串
                params[i] = part.substring(1, part.length() - 1);
            } else if (part.equalsIgnoreCase("true") || part.equalsIgnoreCase("false")) {
                params[i] = Boolean.parseBoolean(part);
            } else {
                throw new IllegalArgumentException("无法解析参数: " + part);
            }
        }

        return params;
    }

    /**
     * 获取包装类型对应的基本类型
     *
     * @param obj 参数对象
     * @return 基本类型（如果是包装类型则返回其基本类型，否则返回对象的实际类型）
     */
    private Class<?> getPrimitiveType(Object obj) {
        if (obj instanceof Integer) {
            return int.class;
        } else if (obj instanceof Long) {
            return long.class;
        } else if (obj instanceof Double) {
            return double.class;
        } else if (obj instanceof Float) {
            return float.class;
        } else if (obj instanceof Boolean) {
            return boolean.class;
        } else if (obj instanceof Character) {
            return char.class;
        } else if (obj instanceof Byte) {
            return byte.class;
        } else if (obj instanceof Short) {
            return short.class;
        }
        return obj.getClass(); // 如果不是包装类型，直接返回实际类型
    }

}
