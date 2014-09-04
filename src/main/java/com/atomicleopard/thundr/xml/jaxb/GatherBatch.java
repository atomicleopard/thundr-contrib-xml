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
package com.atomicleopard.thundr.xml.jaxb;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Batch} which saves all items across all batches. It gathers, or accumulates, the contents of all the batches together
 */
public class GatherBatch<T> implements Batch<T, List<T>> {

	@Override
	public List<T> process(List<T> result, List<T> batch) {
		if (result == null) {
			result = new ArrayList<T>();
		}
		result.addAll(batch);
		return result;
	}

}
