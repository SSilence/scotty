package scotty.transformer.impl;

import org.owasp.webscarab.model.Request;

import scotty.transformer.RequestTransformer;

public class DefaultRequestTransformer implements RequestTransformer {

	@Override
	public byte[] transformRequest(Request request) {

		return request.toString().getBytes();
	}

}
