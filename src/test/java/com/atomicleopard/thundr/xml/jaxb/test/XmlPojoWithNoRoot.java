package com.atomicleopard.thundr.xml.jaxb.test;

import java.math.BigDecimal;

public class XmlPojoWithNoRoot {
	private String Id;
	private String Name;
	private BigDecimal Amount;

	public XmlPojoWithNoRoot() {

	}

	public XmlPojoWithNoRoot(String id, String name, BigDecimal amount) {
		super();
		Id = id;
		Name = name;
		Amount = amount;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public BigDecimal getAmount() {
		return Amount;
	}

	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

}