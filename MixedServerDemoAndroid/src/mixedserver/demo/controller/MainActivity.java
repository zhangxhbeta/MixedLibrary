package mixedserver.demo.controller;

import mixedserver.application.AuthorityService;
import mixedserver.demo.DemoApplication;
import mixedserver.demo.R;
import mixedserver.demo.service.EncryptionService;
import mixedserver.demo.tools.AsyncResponseHandler;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * 应用首页，实现一个加解密功能
 * 
 * @author zhangxiaohui
 * 
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends SherlockActivity {

	private EncryptionService encrytionService = new EncryptionService();

	@ViewById
	EditText editTextMessage;

	@ViewById
	Button buttonEncrypt;
	@ViewById
	TextView textViewResultEncrypt;
	@ViewById
	ProgressBar progressBarEncrypt;

	@ViewById
	Button buttonDencrypt;
	@ViewById
	TextView textViewResultDencrypt;
	@ViewById
	ProgressBar progressBarDencrypt;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionUserCenter:
			DemoApplication app = (DemoApplication) getApplication();
			boolean showUserCenter = app.isTokenRegisted();
			if (showUserCenter) {
				final Intent intent = new Intent(this,
						UserCenterActivity_.class);
				startActivity(intent);
			} else {
				final Intent intent = new Intent(this, LoginActivity_.class);
				startActivity(intent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@AfterViews
	void afterViews() {
		progressBarEncrypt.setVisibility(View.GONE);
		progressBarDencrypt.setVisibility(View.GONE);

		buttonDencrypt.setVisibility(View.INVISIBLE);

		// 在软件启动时，判断一下客户端有没有 Longtime Token
		Client.getClient(GlobalTools.SERVER_URL).setDencryptMessage(true);
		Client.getClient(GlobalTools.SERVER_URL).setEncryptMessage(false);

		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
		String token = settings.getString(AuthorityService.AS_LONGTIME_TOKEN,
				null);
		if (token != null) {
			Client.getClient(GlobalTools.SERVER_URL).registLongtimeToken(token);
			DemoApplication app = (DemoApplication) getApplication();
			app.setTokenRegisted(true);
		}
	}

	@Click(R.id.buttonEncrypt)
	void onEncryptClicked() {
		String message = editTextMessage.getText().toString();
		if (message.trim().length() == 0) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("填写错误");
			dialog.setMessage("消息不能为空");
			dialog.show();
			return;
		}

		// 复位界面
		textViewResultEncrypt.setText("");
		textViewResultDencrypt.setText("");
		buttonDencrypt.setVisibility(View.INVISIBLE);
		progressBarEncrypt.setVisibility(View.VISIBLE);

		// 调用后台方法
		encrytionService.encrypt(message, new AsyncResponseHandler() {
			@Override
			public void onSuccess(Object response) {
				progressBarEncrypt.setVisibility(View.GONE);
				textViewResultEncrypt.setText((String) response);

				buttonDencrypt.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFailure(Throwable error, String message, long code) {
				if (code == RPCException.ERROR_CODE_INVALID_SESSION) {
					showLogin();
				}
				progressBarEncrypt.setVisibility(View.GONE);
				textViewResultEncrypt.setText((String) message);

				buttonDencrypt.setVisibility(View.VISIBLE);
			}
		});
	}

	@Click(R.id.buttonDencrypt)
	void onDencryptClicked() {
		String message = textViewResultEncrypt.getText().toString();
		if (message.trim().length() == 0) {
			return;
		}

		// 界面部分
		progressBarDencrypt.setVisibility(View.VISIBLE);

		// 调用后台方法
		encrytionService.dencrypt(message, new AsyncResponseHandler() {
			@Override
			public void onSuccess(Object response) {
				progressBarDencrypt.setVisibility(View.GONE);
				textViewResultDencrypt.setText((String) response);
			}

			@Override
			public void onFailure(Throwable error, String message, long code) {
				if (code == RPCException.ERROR_CODE_INVALID_SESSION) {
					showLogin();
				}
				progressBarDencrypt.setVisibility(View.GONE);
				textViewResultDencrypt.setText(message);
			}
		});
	}

	void showLogin() {
		final Intent intent = new Intent(this, LoginActivity_.class);
		startActivity(intent);
	}

}
