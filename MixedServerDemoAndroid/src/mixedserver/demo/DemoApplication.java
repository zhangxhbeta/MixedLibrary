package mixedserver.demo;

import mixedserver.application.AuthorityService;
import mixedserver.demo.controller.LoginActivity;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.jsonrpc.client.Client;
import android.app.Application;
import android.content.SharedPreferences;

public class DemoApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();

		// 在软件启动时，判断一下客户端有没有 Longtime Token
		Client client = Client.getClient(GlobalTools.SERVER_URL);

		client.setDencryptMessage(true);
		client.setEncryptMessage(false);

		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
		String token = settings.getString(AuthorityService.AS_LONGTIME_TOKEN,
				null);
		if (token != null) {
			client.registLongtimeToken(token);
		}
	}
}
