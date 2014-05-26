package mixedserver.protocol;

/**
 * A standardized exception class that conforms with JSON-RPC specifications.
 * 
 * @author Wes Widner
 * @see <a
 *      href="http://groups.google.com/group/json-rpc/web/json-rpc-1-2-proposal#error-object">JSON-RPC
 *      Error Specification</a>
 */

@SuppressWarnings("serial")
public class RPCException extends Exception {
	private long code;

	/**
	 * 解析错误
	 */
	public static final long PREDEFINED_ERROR_PARSE_ERROR = -32700;

	/**
	 * 不合适的请求
	 */
	public static final long PREDEFINED_ERROR_INVALID_REQUEST = -32600;

	/**
	 * 方法未找到
	 */
	public static final long PREDEFINED_ERROR_METHOD_NOT_FOUND = -32601;

	/**
	 * 不合适的参数
	 */
	public static final long PREDEFINED_ERROR_INVALID_PARAMS = -32602;

	/**
	 * 内部错误
	 */
	public static final long PREDEFINED_ERROR_INTERNAL_ERROR = -32603;

	/**
	 * 扩展错误：非法会话，未登录或没有权限
	 */
	public static final long ERROR_CODE_INVALID_SESSION = -10001;

	/**
	 * 扩展错误：远程服务器错误
	 */
	public static final long ERROR_CODE_REMOTE_ERROR = -10002;

	/**
	 * 扩展错误：网络错误（客户端使用）
	 */
	public static final long ERROR_CODE_NETWORK_ERROR = -10003;

	/**
	 * 扩展错误： 已登录认证但是没有相应权限
	 */
	public static final long ERROR_CODE_AUTHZ_ERROR = -10004;

	public RPCException() {
		super();
		setCode(-32603); // Generic JSON-RPC error code.
	}

	public RPCException(String message) {
		super(message);
		setCode(-32603); // Generic JSON-RPC error code.
	}

	public RPCException(String message, long code) {
		super(message);
		setCode(code);
	}

	/**
	 * Set the JSON-RPC error code for this exception
	 * 
	 * @param code
	 *            The JSON-RPC error code, usually negative in the range of
	 *            -32768 to -32000 inclusive
	 */
	public void setCode(long code) {
		this.code = code;
	}

	/**
	 * Get the JSON-RPC error code of this exception.
	 * 
	 * @return long Error code, usually negative in the range of -32768 to
	 *         -32000 inclusive
	 */
	public long getCode() {
		return code;
	}
}
