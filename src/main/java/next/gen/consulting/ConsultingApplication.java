package next.gen.consulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ConsultingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsultingApplication.class, args);
    }

}
