package com.shuhang.bubalus.utils;

/**
 * 
 * @author mingfei.z 2018年10月24日 下午10:30:15
 *
 */
public class StringUtils extends org.springframework.util.StringUtils {

	/**
	 * 字符串不为null 和 空字符串
	 * 
	 * @param str
	 * @return
	 * @author mingfei.z 2018年10月24日 下午10:45:06
	 */
	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}
	
}
