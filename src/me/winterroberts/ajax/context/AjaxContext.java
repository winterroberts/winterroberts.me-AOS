package me.winterroberts.ajax.context;

import org.json.JSONException;

import net.aionstudios.api.context.Context;
import net.aionstudios.api.response.Response;

public class AjaxContext extends Context {

	public AjaxContext() {
		super("ajax");
	}

	@Override
	public void contextDefault(Response response, String requestContext) throws JSONException {
		
	}

}
