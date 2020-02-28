package me.winterroberts.ajax.action;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import me.winterroberts.ajax.email.EmailUtils;
import net.aionstudios.api.action.Action;
import net.aionstudios.api.aos.ResponseStatus;
import net.aionstudios.api.errors.UnauthorizedAccessError;
import net.aionstudios.api.file.MultipartFile;
import net.aionstudios.api.response.Response;
import net.aionstudios.api.util.DatabaseUtils;

public class ContactAction  extends Action {
	
	public ContactAction() {
		super("contact");
		this.setPostRequiredParams("ajax_token", "sessionID", "name", "email", "msg");
	}
	
	private String validTokenQuery = "SELECT * FROM `portfolio_ajax`.`ajax_tokens` WHERE `token` = ? AND `sessionID` = ?;";

	@Override
	public void doAction(Response response, String requestContext, Map<String, String> getQuery,
			Map<String, String> postQuery, List<MultipartFile> mfs) throws JSONException {
		if(!DatabaseUtils.prepareAndExecute(validTokenQuery, true, postQuery.get("ajax_token"), postQuery.get("sessionID")).get(0).getResults().isEmpty()) {
			Thread r = new Thread(new Runnable() {

				@Override
				public void run() {
					EmailUtils.getInstance().sendEmail("New Contact Request", "From: "+postQuery.get("name")+"<br><br>Message: "+postQuery.get("msg"), "winrob@uw.edu", postQuery.get("email"));
				}
					
			});
			r.start();
			response.putData("contact", true);
			response.putDataResponse(ResponseStatus.SUCCESS, "Completed.");
			return;
		}
		response.putErrorResponse(new UnauthorizedAccessError(), "The Ajax service couldn't not validate your access.");
	}

}
