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

import java.util.List;

/**
 * {@link Batch} is a callback which allows callers to process results in batches. It also provides the ability to perform a reduce on the given inputs.
 * When using the streaming api (i.e. {@link JaxbReader#stream(Batch)}), you can process the incoming xml in batches keeping memory consumption low.
 * 
 * For example, the following example would result in processing in batches and returning the count of all items across all batches:
 * 
 * <pre>
 * <code>
 * public class MyBatch&lt;Pojo, Integer&gt; {
 *   public Integer process(Integer result, List&lt;Pojo&gt; batch){
 *   	&lt; do processing &gt;
 *      if( result == null ){
 *        result = 0;
 *      }
 *   
 *      return result + batch.size();
 *   }
 * }
 * 
 * </code>
 * </pre>
 * 
 * The following example can be used when you don't need to map/reduce or return any results
 * 
 * <pre>
 * <code>
 * public class MyBatch&lt;Pojo, Void&gt; {
 *   public Integer process(Void result, List&lt;Pojo&gt; batch){
 *   	&lt; do processing &gt;
 *      return null;
 *   }
 * }
 * 
 * </code>
 * </pre>
 * 
 * @see GatherBatch
 */
public interface Batch<T, R> {
	public R process(R result, List<T> batch);
}
