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
