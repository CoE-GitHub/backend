package backend;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import brave.Tracer;
import brave.Tracing;
import brave.http.HttpTracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class HelloController {
	@Autowired
	private YAMLConfig myConfig;
	private static final Logger LOG = Logger.getLogger(HelloController.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	Tracer tracer;

	@Bean
	HttpTracing httpTracing(final Tracing tracing) {
		return HttpTracing.create(tracing);
	}

	@RequestMapping("/")
	public String index(@RequestHeader Map<String, String> headers) {
		headers.forEach((key, value) -> {
			LOG.info("Header Name: " + key + " Header Value: " + value);
		});
		String response = restTemplate.getForObject(myConfig.getBackend(), String.class);
		return response;
	}
	final boolean getRandomBoolean(int probability) {
		double randomValue = Math.random()*100;  //0.0 to 99.9
		return randomValue <= probability;
	}

	@RequestMapping("/delay")
	public String delay(@RequestHeader Map<String, String> headers) {
		String delayProbability = headers.getOrDefault("probability", "50");
		String delayMs = headers.getOrDefault("delay", "10000");

		boolean delayRequest = getRandomBoolean(Integer.valueOf(delayProbability));
		try {
			if (delayRequest) {
				Thread.sleep(Integer.valueOf(delayMs));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String response = restTemplate.getForObject(myConfig.getBackend(), String.class);
		return response;
	}

	@RequestMapping("/fail")
	public String fail(@RequestHeader Map<String, String> headers) {
		String failProbability = headers.getOrDefault("probability", "30");
		boolean fail = getRandomBoolean(Integer.valueOf(failProbability));
		if (fail) {
			throw new ResponseStatusException(
				HttpStatus.INTERNAL_SERVER_ERROR, "Backend failed", null);
		}
		String response = restTemplate.getForObject(myConfig.getBackend(), String.class);
		return response;
	}
}
