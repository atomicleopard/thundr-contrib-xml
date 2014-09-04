/*
 * This file is a community contributed library for use with thundr.
 * Read more: http://3wks.github.io/thundr/
 *
 * Copyright (C) 2014 Atomic Leopard, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
