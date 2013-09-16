package mixedserver.protocol.jsonrpc;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {
	private String id = null;

	private boolean idIsNumber = false;

	private String method = null;

	public enum ParamType {
		OBJECT, ARRAY, NONE
	}

	private ParamType paramtype = ParamType.NONE;

	private JSONArray paramArr = null;

	private JSONObject paramObj = null;
	private String[] param_keys;

	private JSONObject request = null;

	private int param_count = 0;

	public Request() {
	}

	public Request(String request) throws JSONException {
		parseJSON(request);
	}

	public boolean parseJSON(JSONObject request) throws JSONException {
		this.request = request;

		parseRequest();

		return true;
	}

	public boolean parseJSON(String requestStr) throws JSONException {
		request = new JSONObject(requestStr);

		parseRequest();

		return true;
	}

	private void parseRequest() throws JSONException {
		if (request.has("id")) {
			try {
				this.setId(String.valueOf(request.getLong("id")));
				this.idIsNumber = true;
			} catch (JSONException e) {
				this.id = request.getString("id");
			}
		}

		if (request.has("params")) {
			parseParams(request.getString("params"));
		}

		// Retrieve method name from JSON data
		if (request.has("method")) {
			method = request.getString("method");
		}
	}

	@SuppressWarnings("unchecked")
	public boolean parseParams(String paramsStr) {
		param_count = 0;

		try {
			paramObj = new JSONObject(paramsStr);

			param_count = (paramObj.names() == null ? 0 : paramObj.names()
					.length());
			param_keys = new String[param_count];

			Iterator<String> paramsIterator = paramObj.sortedKeys();

			int c = 0;
			while (paramsIterator.hasNext()) {
				String param_key = paramsIterator.next();

				param_keys[c] = param_key;
				// params.put(param_key, paramObj.get(param_key));
				c++;
			}

			setParamtype(ParamType.OBJECT);
			return true;
		} catch (JSONException je) {
			try {
				paramArr = new JSONArray(paramsStr);
				param_count = paramArr.length();

				setParamtype(ParamType.ARRAY);

				return true;
			} catch (JSONException jae) {
				setParamtype(ParamType.NONE);
				return false;
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean idIsNumber() {
		return idIsNumber;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean hasParam(String param) {
		if (paramtype == ParamType.NONE) {
			return false;
		} else if (paramtype == ParamType.OBJECT) {
			return paramObj.has(param);
		} else {
			return false;
		}
	}

	public String getParam(String param) throws JSONException {
		if (paramtype == ParamType.NONE) {
			return null;
		} else if (paramtype == ParamType.OBJECT && paramObj.has(param)) {
			return paramObj.getString(param);
		} else {
			return null;
		}
	}

	public String getParamAt(int pos) throws JSONException {
		if (paramtype == ParamType.NONE || param_count < 1 || pos > param_count
				|| pos < 0) {
			return null;
		} else if (paramtype == ParamType.OBJECT) {
			return paramObj.getString(param_keys[pos]);
		} else if (paramtype == ParamType.ARRAY) {
			return paramArr.getString(pos);
		} else {
			return null;
		}
	}

	public JSONObject getParamObj() {
		if (paramtype == ParamType.NONE) {
			return null;
		} else if (paramtype == ParamType.OBJECT) {
			return paramObj;
		} else if (paramtype == ParamType.ARRAY) {
			return new JSONObject(paramArr);
		} else {
			return null;
		}
	}

	public JSONObject getJSON() {
		return request;
	}

	public void setParamCount(int param_count) {
		this.param_count = param_count;
	}

	public int getParamCount() {
		return param_count;
	}

	public void setParamtype(ParamType paramtype) {
		this.paramtype = paramtype;
	}

	public ParamType getParamtype() {
		return paramtype;
	}
}
