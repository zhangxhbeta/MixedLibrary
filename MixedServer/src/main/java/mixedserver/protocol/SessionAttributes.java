package mixedserver.protocol;

public interface SessionAttributes {

	public abstract Object getAttribute(String s);

	public abstract void setAttribute(String s, Object obj);

	public abstract void removeAttribute(String s);

}