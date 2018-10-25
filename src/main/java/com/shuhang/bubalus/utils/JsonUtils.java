/**
 * @author mingfei.z 2018年10月24日 下午10:33:16
 */
package com.shuhang.bubalus.utils;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON 处理
 * 
 * @author mingfei.z 2018年10月24日 下午10:33:55
 */
public class JsonUtils {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
	/**
	 * 对象转JSON格式
	 * 
	 * @param obj
	 * @param prettyPrint 是否打印优化，即换行
	 * @return
	 * @author mingfei.z 2018年10月24日 下午10:39:35
	 */
	public static String objectToJson(Object obj, boolean prettyPrint) {
		String json = "";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory();
		
		if (null == obj)
			return json;
		
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			if (prettyPrint)
				jg.useDefaultPrettyPrinter();
			
			// ObjectMapper Date类型默认转为timestamp形式，现取消默认
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			// 设置自定义Date格式
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			
			// 这种方式不生效，可能是个bug
			// objectMapper.getSerializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			// objectMapper.getDeserializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			// json = objectMapper.writeValueAsString(obj);
			objectMapper.writeValue(jg, obj);
			json = sw.toString();
		} catch (Exception e) {
			logger.error("[{}]序列化成JSON失败，异常", obj.toString(), e);
		}
		
		return json;
	}

	/**
	 * 对象转JSON格式
	 * 
	 * @param obj
	 * @param prettyPrint 是否打印优化，即换行
	 * @param incl              //Include.Include.ALWAYS 默认
                                //Include.NON_DEFAULT 属性为默认值不序列化
                                //Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
                                //Include.NON_NULL 属性为NULL 不序列化
	 * @return 
	 * @author mingfei.z 2018年10月24日 下午10:55:01
	 */
    public static String objectToJson(Object obj, boolean prettyPrint, JsonInclude.Include incl) {
        String result = "";
		ObjectMapper objectMapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory();
        if (null == obj)
        	return result;
        
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator jg = jsonFactory.createGenerator(sw);
            if (prettyPrint) {
                jg.useDefaultPrettyPrinter();
            }
            objectMapper.setSerializationInclusion(incl);
            objectMapper.writeValue(jg, obj);
            result = sw.toString();
        } catch (Exception e) {
            logger.error("[{}]序列化成JSON失败，异常", obj.toString(), e);
        }
        
        return result;
    }

    /**
     * JSON反序列化成对象
     * @param json
     * @param clazz
     * @return
     * @author mingfei.z
     */
	public static <T> T jsonToObject(String json, Class<T> clazz) {
		
		logger.info("JSON[{}]反序列化", json);
		T obj = null;
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (StringUtils.isEmpty(json))
			return obj;
		
		try {
			obj = objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			logger.error("JSON[{}]反序列化失败，异常", json, e);
		}
		
		return obj;
	}
	
	/**
	 * JSON 反序列化成List
	 * @param json
	 * @param clazz 集合元素类型
	 * @return
	 * @author mingfei.z
	 */
	public static <T> List<T> jsonToList(String json, Class<T> clazz) {

		logger.info("JSON[{}]反序列化", json);
		List<T> list = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (StringUtils.isEmpty(json))
			return list;
		
		try {
			JavaType javaType = getCollectionType(ArrayList.class, clazz);
			
			list =  objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error("JSON[{}]反序列化失败，异常", json, e);
		}
		
		return list;
	}
	
	/**
	 * JSON反序列化成Map
	 * @param json
	 * @param clazz
	 * @return
	 * @author mingfei.z
	 */
	public static <K, V> Map<K, V> jsonToMap(String json, Class<K> kClazz, Class<V> vClazz) {

		logger.info("JSON[{}]反序列化", json);
		Map<K, V> map = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		if (StringUtils.isEmpty(json))
			return map;
		
		try {
			JavaType javaType = getCollectionType(HashMap.class, kClazz, vClazz);
			
			map =  objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error("JSON[{}]反序列化失败，异常", json, e);
		}
		
		return map;
	}
	
	/**
	 * 获取泛型的Collection Type
	 * @param collectionClass 泛型的Collection
	 * @param elementClass 元素类
	 * @return JavaType Java类型
	 */
	public static JavaType getCollectionType(Class<?> collectionClass, Class<?> ... elementClass) {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
	}
	
}
