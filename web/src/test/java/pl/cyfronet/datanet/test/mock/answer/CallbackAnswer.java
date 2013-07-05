package pl.cyfronet.datanet.test.mock.answer;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.web.client.callback.NextCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;

public class CallbackAnswer {

	public static Answer<Void> nextCallbackAnswer(final int paramNr) {
		return new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				NextCallback callback = (NextCallback) invocation
						.getArguments()[paramNr];
				callback.next();
				return null;
			}
		};
	}
	
	public static Answer<Void> modelCallbackAnswer(final int paramNr, final ModelProxy model) {
		return new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback callback = (ModelCallback) invocation
						.getArguments()[paramNr];
				callback.setModel(model);
				return null;
			}
		};
	}
}
