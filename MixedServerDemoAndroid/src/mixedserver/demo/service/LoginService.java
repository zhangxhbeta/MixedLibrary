package mixedserver.demo.service;

import java.util.Map;

import mixedserver.application.AuthorityService;
import mixedserver.demo.tools.AsyncResponseHandler;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;

import com.googlecode.androidannotations.api.BackgroundExecutor;

public class LoginService {
	private Client client = Client.getClient(GlobalTools.SERVER_URL);

	/**
	 * 登录
	 * 
	 * @param handler
	 */
	public void login(final String username, final String password,
			final AsyncResponseHandler handler) {
		BackgroundExecutor.execute(new Runnable() {

			@Override
			public void run() {
				Map<String, String> returnValue;
				try {
					returnValue = client.login(username, password);

					String token = returnValue
							.get(AuthorityService.AS_LONGTIME_TOKEN);

					client.registLongtimeToken(token);

					handler.sendSuccessMessage(returnValue);
				} catch (RPCException e) {
					handler.sendFailureMessage(e, e.getLocalizedMessage(),
							e.getCode());
				}

			}
		});
	}

	/**
	 * 登录
	 * 
	 * @param handler
	 */
	public void logout(final String token, final AsyncResponseHandler handler) {
		BackgroundExecutor.execute(new Runnable() {

			@Override
			public void run() {
				// 清除网络库里面的的cookie
				client.registLongtimeToken("");

				// 调用注销服务
				AuthorityService auth = client.openProxy("authority",
						AuthorityService.class);
				try {
					auth.logout(token);

					handler.sendSuccessMessage(null);
				} catch (RPCException e) {
					handler.sendFailureMessage(e, e.getLocalizedMessage(),
							e.getCode());
				} finally {
					client.closeProxy(auth);
				}
			}
		});
	}
}
