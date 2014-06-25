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
public class SimpleAuthResult {

	private String username;

	private String domainId;

	private String longtimeToken;

	private Map<String, String> map;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getLongtimeToken() {
		return longtimeToken;
	}

	public void setLongtimeToken(String longtimeToken) {
		this.longtimeToken = longtimeToken;
	}

	public void addInfo(String key, String value) {
		if (map == null) {
			map = new HashMap<String, String>();
		}

		map.put(key, value);
	}

	public String getInfo(String key) {
		if (map == null) {
			return null;
		}

		return map.get(key);

	}

	public Set<String> getAllInfoKey() {
		if (map == null) {
			return null;
		}

		return map.keySet();
	}

	public Map<String, String> getAllInfo() {
		return map;
	}

}
