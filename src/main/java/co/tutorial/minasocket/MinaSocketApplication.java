package co.tutorial.minasocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@Configuration
@ImportResource(locations={"classpath:config/application-mina.xml"})
public class MinaSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinaSocketApplication.class, args);
	    Logger logger = LoggerFactory.getLogger(MinaSocketApplication.class);
	    logger.info("启动服务");
	}

}
