/**
 * @author mingfei.z 2018年10月24日 下午10:33:16
 */
package com.shuhang.bubalus.utils;

import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON 处理
 * 
 * @author mingfei.z 2018年10月24日 下午10:33:55
 */
public class JsonUtils {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonFactory jsonFactory = new JsonFactory();
    
	/**
	 * 对象转JSON格式
	 * 
	 * @param obj
	 * @param prettyPrint 是否打印优化，即换行
	 * @return
	 * @author mingfei.z 2018年10月24日 下午10:39:35
	 */
	public static String objectToString(Object obj, boolean prettyPrint) {
		String json = "";
		
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
    public static String objectToString(Object obj, boolean prettyPrint, JsonInclude.Include incl) {
        String result = "";
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

	public static <T> T stringToObject(String content, Class<T> clazz) {
		
		logger.info("JSON[{}]反序列化", content);
		T obj = null;
		
		if (StringUtils.isEmpty(content))
			return obj;
		
		try {
			obj = objectMapper.readValue(content, clazz);
		} catch (Exception e) {
			logger.error("JSON[{}]反序列化失败，异常", content, e);
		}
		
		return obj;
	}
	
}
