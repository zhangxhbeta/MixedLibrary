package mixedserver.demo.controller;

import mixedserver.application.AuthorityService;
import mixedserver.demo.R;
import mixedserver.demo.service.LoginService;
import mixedserver.demo.tools.AsyncResponseHandler;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_user_center)
public class UserCenterActivity extends SherlockActivity {

	private LoginService loginService = new LoginService();

	@ViewById
	TextView textViewUsername;

	@ViewById
	Button buttonLogout;

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
	void AfterViews() {
		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
		String personName = null;
		personName = settings.getString(AuthorityService.AS_USER_NAME, null);

		if (personName != null) {
			textViewUsername.setText("欢迎您，" + personName);
		} else {
			textViewUsername.setText("用户未登录");
		}
	}

	@Click(R.id.buttonLogout)
	void onLogoutClicked() {
		// 删掉本地存储的token
		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_LOGIN, Context.MODE_PRIVATE);
		String token = settings.getString(AuthorityService.AS_LONGTIME_TOKEN,
				null);

		if (token == null) {
			return;
		}

		SharedPreferences.Editor editor = settings.edit();
		editor.remove(AuthorityService.AS_LONGTIME_TOKEN);
		editor.remove(AuthorityService.AS_USER_NAME);

		editor.commit();

		// 调用service处理
		loginService.logout(token, new AsyncResponseHandler() {

			@Override
			public void onSuccess(Object response) {
				NavUtils.navigateUpFromSameTask(UserCenterActivity.this);
			}

			@Override
			public void onFailure(Throwable error, String message, long code) {
				NavUtils.navigateUpFromSameTask(UserCenterActivity.this);
			}
		});
	}
}
