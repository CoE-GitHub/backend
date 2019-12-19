package backend;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Application {
	private static final Logger LOG = Logger.getLogger(SpringApplication.class.getName());
	public static void main(String[] args) {
		 ApplicationContext ctx = SpringApplication.run(Application.class, args);
		 LOG.info(String.format("Application started", ctx.getDisplayName()));
	}
}
