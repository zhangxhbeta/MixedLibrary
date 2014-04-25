package mixedserver.application;

import java.util.Set;

/**
 * 认证结果
 * 
 * @author zhangxiaohui
 * 
 */
public interface AuthResult {

	/**
	 * 获取用户名
	 * 
	 * @return
	 */
	public abstract String getUsername();

	/**
	 * 设置用户名
	 * 
	 * @return
	 */
	public abstract void setUsername(String username);

	/**
	 * 设置领域id
	 * 
	 * @return
	 */
	public abstract String getDomainId();

	/**
	 * 获取领域id
	 * 
	 * @param domainId
	 */
	public abstract void setDomainId(String domainId);

	/**
	 * 获取长声明周期令牌
	 * 
	 * @return
	 */
	public abstract String getLongtimeToken();

	/**
	 * 设置长声明周期令牌
	 * 
	 * @param longtimeToken
	 */
	public abstract void setLongtimeToken(String longtimeToken);

	/**
	 * 添加其他信息
	 * 
	 * @param key
	 * @param value
	 */
	public abstract void addInfo(String key, String value);

	/**
	 * 获取添加的自定义信息
	 * 
	 * @param key
	 * @return
	 */
	public abstract String getInfo(String key);

	/**
	 * 获取key集合
	 * 
	 * @return
	 */
	public abstract Set<String> getAllInfoKey();

}