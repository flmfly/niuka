package niuka.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.handler.MappedInterceptor;

@SpringBootApplication
public class Repository {
	@SuppressWarnings("deprecation")
	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory(@Value("${spring.redis.host}") String host,
			@Value("${spring.redis.port}") int port, @Value("${spring.redis.timeout}") long timeout) {
		LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
		factory.setTimeout(timeout);
		return factory;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lcf) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(lcf);
		return template;
	}

	@Bean
	public MappedInterceptor sessionInterceptor(StringRedisTemplate template) {
		return new MappedInterceptor(new String[] { "/**" }, new SessionInterceptor(template));
	}

	public static void main(String[] args) {
		SpringApplication.run(Repository.class, args);
	}
}
