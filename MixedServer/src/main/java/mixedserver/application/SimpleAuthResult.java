package mixedserver.application;

import java.util.HashMap;
import java.util.Map;

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

	private Map<String, String> allInfo;

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
		if (allInfo == null) {
			allInfo = new HashMap<String, String>();
		}

		allInfo.put(key, value);
	}

	public String getInfo(String key) {
		if (allInfo == null) {
			return null;
		}

		return allInfo.get(key);

	}

	public Map<String, String> getAllInfo() {
		return allInfo;
	}

	public Map<String, String> setAllInfo() {
		return allInfo;
	}

}
