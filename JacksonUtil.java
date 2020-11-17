
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;

public class JacksonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);

    private static JsonFactory JSON_FACTORY = new JsonFactory();

    private static Map<String, ObjectMapper> objectMapperMap = Maps.newConcurrentMap();

    static {
        objectMapperMap.put("yyyyMMddHHmmss", createObjectMapper("yyyyMMddHHmmss"));
        objectMapperMap.put("yyyy-MM-dd HH:mm:ss", createObjectMapper("yyyy-MM-dd HH:mm:ss"));
        objectMapperMap.put("yyyyMMddHHmmssSSS", createObjectMapper("yyyyMMddHHmmssSSS"));
        objectMapperMap.put("yyyy-MM-dd", createObjectMapper("yyyy-MM-dd"));
        objectMapperMap.put("yyyy/MM/dd", createObjectMapper("yyyy/MM/dd"));
        objectMapperMap.put("yyyyMMdd", createObjectMapper("yyyyMMdd"));

    }

    /**
     * 创建一个自定义的JSON ObjectMapper
     */
    private static ObjectMapper createObjectMapper(String DATE_PATTERN) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        return objectMapper;
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static <T> String toJson(T value) {
        if (value == null) {
            return null;
        }
        return toJson(value, objectMapperMap.get("yyyyMMddHHmmss"));
    }

    /**
     * 将对象转换为JSON字符串，指定日期格式
     */
    public static <T> String toJson(T value, String datePattern) {
        if (value == null || StringUtils.isBlank(datePattern)) {
            return null;
        }
        if (!objectMapperMap.keySet().contains(datePattern)) {
            LOGGER.warn("not support date pattern : {} ", datePattern);
            return null;
        }
        if (StringUtils.isBlank(datePattern)) {
            return toJson(value);
        }
        return toJson(value, objectMapperMap.get(datePattern));
    }

    /**
     * private，指定objectMapper 将对象转换为JSON字符串
     */
    private static <T> String toJson(T value, ObjectMapper objectMapper) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen = null;
        try {
            gen = JSON_FACTORY.createGenerator(sw);
            objectMapper.writeValue(gen, value);
            return sw.toString();
        } catch (IOException e) {
            LOGGER.error("object to json exception!", e);
        } finally {
            if (gen != null) {
                try {
                    gen.close();
                } catch (IOException e) {
                    LOGGER.warn("Exception occurred when closing JSON generator!", e);
                }
            }
        }
        return null;
    }

    /**
     * 将JSON字符串转换为指定对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String jsonString, Class<T> valueType, Class<?>... itemTypes) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        // 返回值加强制转换是因为scmpf编译机JDK版本jdk1.6.0_20的BUG，编译时会出错，在jdk1.6.0_25之后已修复
        try {
            return toObjectThrowEx(jsonString, valueType, itemTypes);
        } catch (Exception e) {
            LOGGER.error("json to object exception!", e);
        }
        return null;
    }

    /**
     * 将JSON字符串转换为指定对象，抛出异常，默认日期格式yyyyMMddHHmmss
     *
     * @param jsonString
     * @param valueType
     * @param itemTypes
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T toObjectThrowEx(String jsonString, Class<T> valueType, Class<?>... itemTypes)
            throws IOException {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        // 返回值加强制转换是因为scmpf编译机JDK版本jdk1.6.0_20的BUG，编译时会出错，在jdk1.6.0_25之后已修复
        return toObjectThrowEx(jsonString, objectMapperMap.get("yyyyMMddHHmmss"), valueType, itemTypes);
    }

    /**
     * 将JSON字符串转换为指定对象，指定日期格式
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String jsonString, String datePattern, Class<T> valueType, Class<?>... itemTypes) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        if (!objectMapperMap.keySet().contains(datePattern)) {
            LOGGER.warn("json to object can not support date pattern : {} ", datePattern);
            return null;
        }
        // 返回值加强制转换是因为scmpf编译机JDK版本jdk1.6.0_20的BUG，编译时会出错，在jdk1.6.0_25之后已修复
        try {
            return toObjectThrowEx(jsonString, objectMapperMap.get(datePattern), valueType, itemTypes);
        } catch (Exception e) {
            LOGGER.error("json to object exception!", e);
        }
        return null;
    }

    /**
     * private 指定objectMapper 将JSON字符串转换为指定对象
     *
     * @param jsonString
     * @param objectMapper
     * @param valueType
     * @param itemTypes
     * @param <T>
     * @return
     * @throws IOException
     */
    private static <T> T toObjectThrowEx(String jsonString, ObjectMapper objectMapper, Class<T> valueType,
                                         Class<?>... itemTypes) throws IOException {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        // 返回值加强制转换是因为scmpf编译机JDK版本jdk1.6.0_20的BUG，编译时会出错，在jdk1.6.0_25之后已修复
        if (itemTypes.length == 0) {
            // 非集合类型
            return (T) objectMapper.readValue(jsonString, valueType);
        } else {
            // 集合类型, 如List,Map
            JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametrizedType(valueType, valueType, itemTypes);
            return (T) objectMapper.readValue(jsonString, javaType);
        }
    }

   

}
