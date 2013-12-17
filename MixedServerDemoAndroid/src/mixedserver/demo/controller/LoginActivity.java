package mixedserver.demo.controller;

import java.util.Map;

import mixedserver.application.AuthorityService;
import mixedserver.demo.R;
import mixedserver.demo.service.LoginService;
import mixedserver.demo.tools.AsyncResponseHandler;
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
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * 登录，在用户的长期令牌到期后，会显示登录界面
 * 
 * @author zhangxiaohui
 * 
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends SherlockActivity {

	public static final String PREFS_LOGIN = "PREFS_LOGIN";

	private LoginService loginService;

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
		loginService = new LoginService();

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
		loginService.login(username, password, new AsyncResponseHandler() {
			@Override
			public void onSuccess(Object response) {
				Map<String, String> returnValue = (Map<String, String>) response;

				String token = returnValue
						.get(AuthorityService.AS_LONGTIME_TOKEN);
				String personName = returnValue
						.get(AuthorityService.AS_USER_NAME);

				SharedPreferences settings = getSharedPreferences(PREFS_LOGIN,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(AuthorityService.AS_LONGTIME_TOKEN, token);
				editor.putString(AuthorityService.AS_USER_NAME, personName);
				editor.commit();

				// 返回主界面
				NavUtils.navigateUpFromSameTask(LoginActivity.this);
			}

			@Override
			public void onFailure(Throwable error, String message, long code) {
				// 提示失败
				final AlertDialog dialog = new AlertDialog.Builder(
						LoginActivity.this).create();
				dialog.setTitle("登录失败");
				dialog.setMessage(message);
				dialog.show();

				progressBar.setVisibility(View.GONE);
			}
		});
	}
}
