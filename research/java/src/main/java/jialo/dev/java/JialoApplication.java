package jialo.dev.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jialo.dev.java.util.JialoUtils;

@SpringBootApplication
public class JialoApplication implements CommandLineRunner {

	protected Logger logger = LoggerFactory.getLogger(JialoApplication.class);

	@Value("${spring.application.name:jialo}")
	String appName;

	public static void main(String[] args) {
		SpringApplication.run(JialoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info(appName + " to be running ... ...");
		JialoUtils.postData("/level1b(0)/level2a(0)");
		System.out.println("exiting ... ...");
		System.exit(0);
	}

}
