package mixedserver.protocol;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * RPC 方法属性
 * 
 * @author zhangxha
 * 
 */
public class RPCMethod {
	private String implementClass;

	private String methodName;

	private Method method;

	private boolean proxy;

	private boolean sessionRelated;

	private String interfaceClass;

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public boolean isSessionRelated() {
		return sessionRelated;
	}

	public void setSessionRelated(boolean sessionRelated) {
		this.sessionRelated = sessionRelated;
	}

	public String getImplementClass() {
		return implementClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getInterfaceClass() {
		return interfaceClass;
	}

	private RPCMethod(String methodName, String implementClass, Method method,
			boolean proxy, boolean sessionRelated) {
		super();
		this.methodName = methodName;
		this.implementClass = implementClass;
		this.method = method;
		this.proxy = proxy;
		this.sessionRelated = sessionRelated;
		this.interfaceClass = method.getDeclaringClass().getName();
	}

	public static RPCMethod newProxyMethod(String methodName,
			String implementClass, Method method) {
		return new RPCMethod(methodName, implementClass, method, true, false);
	}

	public static RPCMethod newMethod(String methodName, String implementClass,
			Method method) {
		return new RPCMethod(methodName, implementClass, method, false, false);
	}

	public static RPCMethod newSessionRelatedMethod(String methodName,
			String implementClass, Method method) {
		return new RPCMethod(methodName, implementClass, method, false, true);
	}

	public Class<?>[] getParameterTypes() {
		return method != null ? method.getParameterTypes() : null;
	}

	public Type[] getGenericParameterTypes() {
		return method != null ? method.getGenericParameterTypes() : null;
	}
}
