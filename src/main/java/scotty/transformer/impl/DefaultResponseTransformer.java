package scotty.transformer.impl;

import java.io.ByteArrayInputStream;

import org.owasp.webscarab.model.Response;

import scotty.transformer.ResponseTransformer;



public class DefaultResponseTransformer implements ResponseTransformer {

	@Override
	public Response transformResponse(byte[] response) {

		Response r = new Response();

		try {
			r.read(new ByteArrayInputStream(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

}
