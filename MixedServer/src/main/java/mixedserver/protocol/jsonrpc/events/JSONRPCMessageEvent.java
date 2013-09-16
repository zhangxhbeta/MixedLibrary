package mixedserver.protocol.jsonrpc.events;

import java.util.EventObject;

public class JSONRPCMessageEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1522676910933572512L;
	private JSONRPCMessage _message;

	public JSONRPCMessageEvent(Object source, JSONRPCMessage message) {
		super(source);
		_message = message;
	}

	public JSONRPCMessage message() {
		return _message;
	}

}
