/**
 * @author mingfei.z 2018年10月24日 下午10:57:32
 */
package com.shuhang.bubalus.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.shuhang.bubalus.po.Book;

/**
 * 
 * 
 * @author mingfei.z 2018年10月24日 下午10:57:37
 */
public class JsonUtilsTest {

	@Test
	public void testObjectToString() {
		List<Book> bookList = new ArrayList<>();
		Book Book1 = new Book();
		Book1.setTitle("1");
		Book1.setPrice(BigDecimal.valueOf(100));
		Book Book2 = new Book();
		Book2.setTitle("2");
		Book2.setPrice(BigDecimal.valueOf(200));
		Book Book3 = new Book();
		Book3.setTitle("3");
		
		bookList.addAll(Arrays.asList(Book1, Book2, Book3));
		String json1 = JsonUtils.objectToString(bookList, true);
		String json2 = JsonUtils.objectToString(bookList, false);
		String json3 = JsonUtils.objectToString(bookList, false, Include.NON_NULL);
		
		Object object = JsonUtils.stringToObject(json2, Book.class);
		
		System.err.println(json1);
		System.err.println(json2);
		System.err.println(json3);
		System.err.println(object.toString());
	}
	
}
