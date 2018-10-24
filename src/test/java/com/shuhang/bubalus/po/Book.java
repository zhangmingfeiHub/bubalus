/**
 * @author mingfei.z 2018年10月24日 下午11:00:22
 */
package com.shuhang.bubalus.po;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * 
 * @author mingfei.z 2018年10月24日 下午11:00:27
 */
public class Book implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6637984486413702139L;
	private String title;
	private BigDecimal price;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Book [title=" + title + ", price=" + price + "]";
	}

}
