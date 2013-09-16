package mixedserver.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mixedserver.application.AuthorityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 令牌登录过滤器，配合客户端的长生命周期令牌，可以达到客户端免用户名密码登录的功能
 * 
 * @author zhangxh
 */
public class TokenLoginFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(TokenLoginFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(AuthorityService.AS_LONGTIME_TOKEN)) {

				String token = cookie.getValue();

				logger.debug(String.format(
						"find longtime token %s, do token login", token));

				// 演示目的：真实情况根据 token 查询令牌记录，验证token是否有效，如果有效取得 userId
				// 至此登录成功
				HttpSession session = request.getSession();
				if (token.equals("8309-2342-3976-3413")) {
					String userId = "demo";
					session.setAttribute(AuthorityService.AS_LONGTIME_TOKEN,
							token);
					session.setAttribute("userId", userId);
				}
				// 切记修改！！！！
			}
		}

		// 继续
		chain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
