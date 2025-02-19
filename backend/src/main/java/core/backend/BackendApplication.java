package core.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

@SpringBootApplication
public class BackendApplication {
    static {
        //.env 파일 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        //환경 변수 설정
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }

    public static void main(String[] args) {
        ConfigurableEnvironment env = new StandardEnvironment();

        String jwtSecretFromProperty = env.getProperty("jwt.secret");
        String jwtSecretFromEnv = System.getenv("JWT_SECRET");
        String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
        String googleClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");

        System.out.println("Checking jwt_secret from properties: " + (jwtSecretFromProperty != null ? jwtSecretFromProperty : "NOT FOUND"));
        System.out.println("Checking jwt_secret from Environment: " + (jwtSecretFromEnv != null ? jwtSecretFromEnv : "NOT FOUND"));
        System.out.println("Checking google_client_id from Environment: " + (googleClientId != null ? googleClientId : "NOT FOUND"));
        System.out.println("Checking google_client_secret from Environment: " + (googleClientSecret != null ? googleClientSecret : "NOT FOUND"));

        SpringApplication.run(BackendApplication.class, args);
    }

}
