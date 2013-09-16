package mixedserver.protocol.jsonrpc;

import javax.servlet.http.HttpSession;

import mixedserver.protocol.SessionAttributes;

public class JsonRpcSessionAttributes implements SessionAttributes {

	HttpSession session;

	public JsonRpcSessionAttributes(HttpSession session) {
		super();
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.protocol.jsonrpc.SessionAttributes#getAttribute(java.lang
	 * .String)
	 */
	public Object getAttribute(String s) {
		return session.getAttribute(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.protocol.jsonrpc.SessionAttributes#setAttribute(java.lang
	 * .String, java.lang.Object)
	 */
	public void setAttribute(String s, Object obj) {
		session.setAttribute(s, obj);
	}

	@Override
	public void removeAttribute(String s) {
		session.removeAttribute(s);
	}
}
