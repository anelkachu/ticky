package tkt;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAutoConfiguration
public class TickyApplication extends SpringBootServletInitializer {

	static {
		System.setProperty("OPENSHIFT_MYSQL_DB_HOST", "localhost");
		System.setProperty("OPENSHIFT_MYSQL_DB_PORT", "3306");
		System.setProperty("OPENSHIFT_MYSQL_DB_USERNAME", "root");
		System.setProperty("OPENSHIFT_MYSQL_DB_PASSWORD", "");
		System.setProperty("OPENSHIFT_DATA_DIR",
				System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "RESOURCE_TKT_SERVER");

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TickyApplication.class);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TickyApplication.class, args);
	}

	@Bean
	public HttpMessageConverters customConverters() {
		ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		return new HttpMessageConverters(arrayHttpMessageConverter);
	}

}
