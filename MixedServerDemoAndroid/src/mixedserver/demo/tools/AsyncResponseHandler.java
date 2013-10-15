/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package mixedserver.demo.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 异步调用响应者
 */
abstract public class AsyncResponseHandler {
	static final int SUCCESS_MESSAGE = 0;
	static final int FAILURE_MESSAGE = 1;
	static final int START_MESSAGE = 2; // 暂不实现
	static final int FINISH_MESSAGE = 3; // 暂不实现
	static final int PROGRESS_MESSAGE = 4;

	private Handler handler;

	public AsyncResponseHandler() {
		// 设置 Handler
		if (Looper.myLooper() != null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					AsyncResponseHandler.this.handleMessage(msg);
				}
			};
		}
	}

	//
	// 回调函数，一般用在匿名类里面
	//

	/**
	 * 当请求成功时触发，覆盖本方法去处理你的逻辑
	 */
	public void onSuccess(Object response) {
	}

	/**
	 * 当请求失败时触发，覆盖本方法去处理你的逻辑
	 * 
	 * @param 异常
	 */
	public void onFailure(Throwable error, String message, long code) {
	}

	/**
	 * 当进度更新时触发，覆盖本方法去处理你的逻辑
	 * 
	 * @param 进度
	 */
	public void onProgress(int progress) {
	}

	//
	// 由service调用 (在service在后台线程调用)
	//

	public void sendSuccessMessage(Object response) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { response }));
	}

	public void sendFailureMessage(Throwable e, String message, long code) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e, message,
				code }));
	}

	public void sendProgessMessage(int progress) {
		sendMessage(obtainMessage(PROGRESS_MESSAGE, progress));
	}

	//
	// 消息的预处理，调用者不需要操心 (在调用线程执行，一般为ui线程)
	//

	protected void handleSuccessMessage(Object response) {
		onSuccess(response);
	}

	protected void handleFailureMessage(Object e, Object message, Object code) {
		onFailure((Throwable) e, (String) message, (Long) code);
	}

	protected void handleProgressMessage(Object progress) {
		onProgress((Integer) progress);
	}

	// android 的 Handler 处理消息方法
	protected void handleMessage(Message msg) {
		Object[] response;

		switch (msg.what) {
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleSuccessMessage(response[0]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			handleFailureMessage(response[0], response[1], response[2]);
			break;
		case PROGRESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleProgressMessage(response[0]);
			break;
		default:
			break;
		}
	}

	protected void sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}

	protected Message obtainMessage(int responseMessage, Object response) {
		Message msg = null;
		if (handler != null) {
			msg = this.handler.obtainMessage(responseMessage, response);
		} else {
			msg = Message.obtain();
			msg.what = responseMessage;
			msg.obj = response;
		}
		return msg;
	}
}
