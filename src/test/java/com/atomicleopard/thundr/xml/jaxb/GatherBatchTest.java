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

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class GatherBatchTest {

	@Test
	public void shouldGatherAllBatchesIntoOneList() {
		GatherBatch<String> gatherBatch = new GatherBatch<String>();
		List<String> result = gatherBatch.process(null, list("1", "2"));
		assertThat(result, is(Arrays.asList("1", "2")));

		result = gatherBatch.process(result, list("3", "4"));
		assertThat(result, is(Arrays.asList("1", "2", "3", "4")));

		result = gatherBatch.process(result, list("4", "5"));
		assertThat(result, is(Arrays.asList("1", "2", "3", "4", "4", "5")));
	}
}
