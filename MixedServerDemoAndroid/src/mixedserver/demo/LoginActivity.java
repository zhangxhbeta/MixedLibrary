package mixedserver.demo;

import java.util.Map;

import mixedserver.application.AuthorityService;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * 登录，在用户的长期令牌到期后，会显示
 * 
 * @author zhangxiaohui
 * 
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends SherlockActivity {

	public static final String PREFS_LOGIN = "PREFS_LOGIN";

	private Client client = Client.getClient(GlobalTools.SERVER_URL);

	@ViewById
	EditText editTextUsername;
	@ViewById
	EditText editTextPassword;

	@ViewById
	Button buttonLogin;

	@ViewById
	ProgressBar progressBar;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@AfterViews
	void afterViews() {
		getSherlock().getActionBar().setDisplayHomeAsUpEnabled(true);
		progressBar.setVisibility(View.GONE);
	}

	@Click(R.id.buttonLogin)
	void onLoginClicked() {
		String username = editTextUsername.getText().toString();
		String password = editTextPassword.getText().toString();
		if (username.trim().length() == 0 || password.trim().length() == 0) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("填写错误");
			dialog.setMessage("用户名和密码不能为空");
			dialog.show();
			return;
		}

		// 执行登录
		progressBar.setVisibility(View.VISIBLE);
		doLogin(username, password);
	}

	@Background
	void doLogin(String username, String password) {
		AuthorityService auth = client.openProxy("authority",
				AuthorityService.class);
		Map<String, String> returnValue;
		String errorDesc = null;
		try {
			returnValue = auth.login(username, password);

			String token = returnValue.get(AuthorityService.AS_LONGTIME_TOKEN);
			String personName = returnValue.get(AuthorityService.AS_USER_NAME);

			SharedPreferences settings = getSharedPreferences(PREFS_LOGIN,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(AuthorityService.AS_LONGTIME_TOKEN, token);
			editor.putString(AuthorityService.AS_USER_NAME, personName);
			editor.commit();

			showResult(true, errorDesc);

		} catch (RPCException e1) {
			errorDesc = e1.getMessage();
			showResult(false, errorDesc);
		} finally {
			client.closeProxy(auth);
		}
	}

	@UiThread
	void showResult(boolean success, String result) {
		if (success) {
			NavUtils.navigateUpFromSameTask(this);
		} else {
			// 提示失败
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("登录失败");
			dialog.setMessage(result);
			dialog.show();

			progressBar.setVisibility(View.GONE);
		}
	}

}
