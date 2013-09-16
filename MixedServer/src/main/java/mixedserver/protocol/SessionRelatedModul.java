package mixedserver.protocol;

/**
 * 在通用应用模块里面，如果要访问会话相关属性，需要实现此接口
 * 
 * @author zhangxha
 * 
 */
public interface SessionRelatedModul {
	public void setSession(SessionAttributes session);
}
