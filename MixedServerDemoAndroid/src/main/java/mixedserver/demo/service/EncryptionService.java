package mixedserver.demo.service;

import com.googlecode.androidannotations.api.BackgroundExecutor;

import mixedserver.application.demo.EncryptionTool;
import mixedserver.demo.tools.AsyncResponseHandler;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;

public class EncryptionService {
	private Client client = Client.getClient(GlobalTools.SERVER_URL);

	/**
	 * 加密
	 * 
	 * @param handler
	 */
	public void encrypt(final String message, final AsyncResponseHandler handler) {
		// 后台执行
		BackgroundExecutor.execute(new Runnable() {

			@Override
			public void run() {
				EncryptionTool etool = client.openProxy("encryptionTool",
						EncryptionTool.class);
				String result;

				try {
					result = etool.encrypt(message);

					// 发送结果给controller
					handler.sendSuccessMessage(result);

				} catch (RPCException e) {
					// 发送异常到 controller 处理
					handler.sendFailureMessage(e, e.getLocalizedMessage(),
							e.getCode());
				} finally {
					client.closeProxy(etool);
				}
			}
		});
	}

	/**
	 * 解密
	 * 
	 * @param handler
	 */
	public void dencrypt(final String message,
			final AsyncResponseHandler handler) {
		// 后台执行
		BackgroundExecutor.execute(new Runnable() {

			@Override
			public void run() {
				EncryptionTool etool = client.openProxy("encryptionTool",
						EncryptionTool.class);
				String result;

				try {
					result = etool.dencrypt(message);

					// 发送结果给controller
					handler.sendSuccessMessage(result);

				} catch (RPCException e) {
					// 发送异常到 controller 处理
					handler.sendFailureMessage(e, e.getLocalizedMessage(),
							e.getCode());
				} finally {
					client.closeProxy(etool);
				}
			}
		});
	}
}
