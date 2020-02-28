package me.winterroberts.ajax;

import me.winterroberts.ajax.action.ContactAction;
import me.winterroberts.ajax.context.AjaxContext;
import me.winterroberts.ajax.email.EmailUtils;
import net.aionstudios.api.API;
import net.aionstudios.api.context.ContextManager;
import net.aionstudios.api.util.DatabaseUtils;

public class PortfolioAjax {
	
	public static void main(String[] args) {
		API.initAPI("Winter Roberts Portfolio", 26797, true, "Winter Roberts Portfolio");
		
		/*Ajax*/
		AjaxContext ac = new AjaxContext();
		ContactAction caac = new ContactAction();
		ac.registerAction(caac);
		
		EmailUtils.getInstance();
		
		ContextManager.registerContext(ac);
		createTables();
	}
	
	private static String createAjaxTokenTable = "CREATE TABLE `portfolio_ajax`.`ajax_tokens` (\r\n" + 
			"  `token` varchar(64) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,\r\n" + 
			"  `sessionID` varchar(64) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,\r\n" + 
			"  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\r\n" + 
			"  PRIMARY KEY (`token`),\r\n" + 
			"  UNIQUE KEY `token_UNIQUE` (`token`)\r\n" + 
			") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;";
	
	private static void createTables() {
		DatabaseUtils.prepareAndExecute(createAjaxTokenTable, false);
	}

}
