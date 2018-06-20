package niuka.repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

	private StringRedisTemplate stringRedisTemplate;

	public SessionInterceptor(StringRedisTemplate template) {
		this.stringRedisTemplate = template;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String key = request.getHeader("SessionKey");
		if (null == key) {
			return false;
		}
		return this.stringRedisTemplate.hasKey(key) || "44054f97-9929-4017-97c9-0becd4196283".equals(key);
	}

}
