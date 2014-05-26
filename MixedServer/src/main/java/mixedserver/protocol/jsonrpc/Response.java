package mixedserver.protocol.jsonrpc;

import mixedserver.protocol.RPCException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {

	public static Logger logger = LoggerFactory.getLogger(Response.class);

	private static final String jsonrpc = "2.0";

	private String id;

	private boolean idIsNumber = false;

	private Object result;

	private JSONObject response;

	public Response(Exception e) {
		try {
			handleException(e);
		} catch (JSONException e1) {
			logger.error("", e1);
		}
	}

	public Response(Throwable t) {
		try {
			handleThrowable(t);
		} catch (JSONException e) {
			logger.error("", e);
		}
	}

	public Response() {
		response = new JSONObject();
		setVersion();
	}

	private void handleException(Exception e) throws JSONException {
		response = new JSONObject();
		setVersion();

		JSONObject error = new JSONObject();
		if (e.getClass().getName().endsWith("RPCException")) {
			RPCException je = (RPCException) e;
			error.put("code", je.getCode());
		} else {
			error.put("code", -32603);
		}
		error.put("message", e.getMessage());

		JSONObject errorData = new JSONObject();
		errorData.put("classname", e.getClass().getName());
		errorData.put("hashcode", e.hashCode());
		errorData.put("stacktrace", e.getStackTrace());
		error.put("data", errorData);
		response.put("error", error);

		logger.debug("", e);
	}

	private void handleThrowable(Throwable t) throws JSONException {
		response = new JSONObject();
		setVersion();

		JSONObject error = new JSONObject();

		// PUT MESSAGE
		error.put("message", t.getMessage());

		if (t.getClass().getName().endsWith("RPCException")) {
			RPCException je = (RPCException) t;
			error.put("code", je.getCode());
		} else if (t.getClass().getName().endsWith("AuthenticationException")) {
			// 未登录或者没有权限
			error.put("code", RPCException.ERROR_CODE_INVALID_SESSION);
			if (t.getMessage() == null || t.getMessage().length() == 0) {
				error.put("message", "未登录或者没有相应权限");
			}
		} else if (t.getClass().getName().endsWith("AuthorizationException")) {
			// 没有权限
			error.put("code", RPCException.ERROR_CODE_AUTHZ_ERROR);
			if (t.getMessage() == null || t.getMessage().length() == 0) {
				error.put("message", "没有相应权限");
			}
		} else {
			// 其他内部错误
			error.put("code", RPCException.PREDEFINED_ERROR_INTERNAL_ERROR);
		}

		JSONObject errorData = new JSONObject();
		errorData.put("classname", t.getClass().getName());
		errorData.put("hashcode", t.hashCode());
		errorData.put("stacktrace", t.getStackTrace());
		error.put("data", errorData);
		response.put("error", error);

		logger.debug("", t);
	}

	public void clearErrorData() throws JSONException {
		if (response.has("error")
				&& response.getJSONObject("error").has("data"))
			response.getJSONObject("error").remove("data");
	}

	private void setVersion() {
		try {
			response.put("jsonrpc", jsonrpc);
		} catch (JSONException e) {
			logger.error("", e);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id, boolean idIsNumber) throws JSONException {
		this.idIsNumber = idIsNumber;
		this.id = id;
		if (idIsNumber) {
			response.put("id", Long.parseLong(id));
		} else {
			response.put("id", this.id);
		}
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
		try {
			response.put("result", this.result);
		} catch (JSONException e) {
			logger.error("", e);
		}
	}

	public JSONObject getJSON() {
		return response;
	}

	public String getJSONString() {
		return response.toString();
	}

	public String getJSONString(int i) {
		try {
			return response.toString(i);
		} catch (JSONException e) {
			logger.error("转换到json格式异常，", e);
			return null;
		}
	}

}
