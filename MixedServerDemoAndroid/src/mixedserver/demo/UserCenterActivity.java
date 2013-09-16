package mixedserver.demo;

import mixedserver.application.AuthorityService;
import mixedserver.application.demo.EncryptionTool;
import mixedserver.demo.tools.GlobalTools;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.Client;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_user_center)
public class UserCenterActivity extends SherlockActivity {

	private Client client = Client.getClient(GlobalTools.SERVER_URL);

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
		doLogout();
	}

	@Background
	void doLogout() {
		// 删掉本地存储的token
		SharedPreferences settings = getSharedPreferences(
				LoginActivity.PREFS_LOGIN, MODE_PRIVATE);
		String token = settings.getString(AuthorityService.AS_LONGTIME_TOKEN,
				null);

		if (token == null) {
			return;
		}

		SharedPreferences.Editor editor = settings.edit();
		editor.remove(AuthorityService.AS_LONGTIME_TOKEN);
		editor.remove(AuthorityService.AS_USER_NAME);

		editor.commit();

		// 清除网络库里面的的cookie
		this.client.registLongtimeToken("");
		DemoApplication app = (DemoApplication) getApplication();
		app.setTokenRegisted(false);

		// 调用注销服务
		AuthorityService auth = client.openProxy("authority",
				AuthorityService.class);
		try {
			auth.logout(token);
		} catch (RPCException e1) {
			// 这里不处理，因为
		} finally {
			client.closeProxy(auth);
		}

		// 执行完毕
		showResult();
	}

	@UiThread
	void showResult() {
		NavUtils.navigateUpFromSameTask(this);
	}
}
