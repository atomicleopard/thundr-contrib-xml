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
