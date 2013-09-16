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

	public static final long PREDEFINED_ERROR_PARSE_ERROR = -32700;

	public static final long PREDEFINED_ERROR_INVALID_REQUEST = -32600;

	public static final long PREDEFINED_ERROR_METHOD_NOT_FOUND = -32601;

	public static final long PREDEFINED_ERROR_INVALID_PARAMS = -32602;

	public static final long PREDEFINED_ERROR_INTERNAL_ERROR = -32603;

	public static final long ERROR_CODE_INVALID_SESSION = -10001;

	public static final long ERROR_CODE_REMOTE_ERROR = -10002;

	public static final long ERROR_CODE_NETWORK_ERROR = -10003;

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
