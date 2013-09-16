package mixedserver.demo;

import android.app.Application;

public class DemoApplication extends Application {
	private boolean tokenRegisted;

	public boolean isTokenRegisted() {
		return tokenRegisted;
	}

	public void setTokenRegisted(boolean tokenRegisted) {
		this.tokenRegisted = tokenRegisted;
	}
}
