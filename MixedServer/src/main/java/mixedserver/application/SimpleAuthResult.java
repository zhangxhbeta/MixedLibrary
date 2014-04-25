package mixedserver.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 认证结果实现
 * 
 * @author zhangxiaohui
 * 
 */
public class SimpleAuthResult implements AuthResult {

	private String username;

	private String domainId;

	private String longtimeToken;

	private Map<String, String> map;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mixedserver.application.AuthResultInterface#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.application.AuthResultInterface#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mixedserver.application.AuthResultInterface#getDomainId()
	 */
	@Override
	public String getDomainId() {
		return domainId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.application.AuthResultInterface#setDomainId(java.lang.String)
	 */
	@Override
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mixedserver.application.AuthResultInterface#getLongtimeToken()
	 */
	@Override
	public String getLongtimeToken() {
		return longtimeToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.application.AuthResultInterface#setLongtimeToken(java.lang
	 * .String)
	 */
	@Override
	public void setLongtimeToken(String longtimeToken) {
		this.longtimeToken = longtimeToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.application.AuthResultInterface#addInfo(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addInfo(String key, String value) {
		if (map == null) {
			map = new HashMap<String, String>();
		}

		map.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mixedserver.application.AuthResultInterface#getInfo(java.lang.String)
	 */
	@Override
	public String getInfo(String key) {
		if (map == null) {
			return null;
		}

		return map.get(key);

	}

	@Override
	public Set<String> getAllInfoKey() {
		if (map == null) {
			return null;
		}

		return map.keySet();
	}

}
