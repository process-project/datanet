package pl.cyfronet.datanet.test;

import com.google.gwt.user.client.ui.HasText;

public class MockingUtil {
	public static HasText mockHasText(final String text) {
		return new HasText() {
			@Override
			public void setText(String arg0) {
			}
			@Override
			public String getText() {
				return text;
			}
		};
	}
}