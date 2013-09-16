package mixedserver.protocol;

/**
 * 模块代理接口，用来支持模块某些功能不在本地实现，或不想自己实现等
 * 
 * @author zhangxha
 * 
 */
public interface ModulProxy {
	/**
	 * 代理调用
	 * 
	 * @param cls
	 * @param method
	 * @param paramsTypes
	 * @param paramsValues
	 * @param sessionAttributes
	 * @return
	 * @throws RPCException
	 */
	public Object invoke(String cls, String method, Class[] paramsTypes,
			Object[] paramsValues, SessionAttributes sessionAttributes)
			throws RPCException;
}
