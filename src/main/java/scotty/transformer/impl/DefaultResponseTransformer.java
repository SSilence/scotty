package scotty.transformer.impl;

import java.text.ParseException;

import org.owasp.webscarab.model.Response;

import scotty.transformer.ResponseTransformer;

public class DefaultResponseTransformer implements ResponseTransformer {

	@Override
	public Response transformResponse(byte[] response) {

		Response r = new Response();

		try {
			String s = new String(response);
			r.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

}
