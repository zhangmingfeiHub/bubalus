/**
 * @author mingfei.z 2018年10月24日 下午10:57:32
 */
package com.shuhang.bubalus.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public void test1() {
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
		String json1 = JsonUtils.objectToJson(bookList, true);
		String json2 = JsonUtils.objectToJson(bookList, false);
		String json3 = JsonUtils.objectToJson(bookList, false, Include.NON_NULL);
		
		List<Book> list = JsonUtils.jsonToList(json3, Book.class);
		
		System.err.println(json1);
		System.err.println(json2);
		System.err.println(json3);
		System.err.println(list.toString());
		
		System.out.println("===================================");
		
		String book1Json = JsonUtils.objectToJson(Book1, false);
		Book book1 = JsonUtils.jsonToObject(book1Json, Book.class);
		List<Book> book3List = JsonUtils.jsonToList(json3, Book.class);

		Map<String, Book> map = new HashMap<>();
		map.put("book1", Book1);
		map.put("book2", Book2);
		String mapJson = JsonUtils.objectToJson(map, false);
		Map<String, Book> map2 = JsonUtils.jsonToMap(mapJson, String.class, Book.class);
		
		System.err.println("book1Json = " + book1Json);
		System.err.println("book1 = " + book1);
		System.err.println("book3List = " + book3List);
		System.err.println("mapJson = " + mapJson);
		System.err.println("map2 = " + map2);
	}

	@Test
	public void test2() {
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
		String json3 = JsonUtils.objectToJson(bookList, false, Include.NON_NULL);
		
		System.err.println(json3);
		
		System.out.println("===================================");
		
		String book3 = JsonUtils.objectToJson(Book3, false, Include.NON_NULL);

		System.err.println(book3);
		
		System.out.println("===================================");
		
		Map<String, Book> map = new HashMap<>();
		map.put("book1", Book1);
		map.put("book2", Book2);
		map.put("book3", Book3);
		String mapJson = JsonUtils.objectToJson(map, false, Include.NON_NULL);

		System.err.println(mapJson);
		
	}
	
}
