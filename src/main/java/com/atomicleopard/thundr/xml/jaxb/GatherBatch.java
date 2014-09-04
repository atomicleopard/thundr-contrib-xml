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
