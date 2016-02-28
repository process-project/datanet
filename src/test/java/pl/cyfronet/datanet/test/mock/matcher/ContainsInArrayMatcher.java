package pl.cyfronet.datanet.test.mock.matcher;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ContainsInArrayMatcher extends BaseMatcher<List<String>> {

	private String[] names;

	public ContainsInArrayMatcher(String... names) {
		this.names = names;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean matches(Object item) {
		List<String> elements = (List<String>) item;
		return elements.containsAll(Arrays.asList(names));
	}

	@Override
	public void describeTo(Description description) {

	}

}
