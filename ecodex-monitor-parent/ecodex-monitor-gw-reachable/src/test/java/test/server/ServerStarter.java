package test.server;

import org.springframework.boot.actuate.autoconfigure.security.servlet.SecurityRequestMatchersManagementContextConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication()
public class ServerStarter {

    public static ConfigurableApplicationContext CTX;

    public static void main(String ...args) {
        startServer1(args);
    }

    public static ConfigurableApplicationContext startServer1(String ...args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        CTX = builder.sources(ServerStarter.class)
                .properties("spring.config.location=classpath:/server1/application.properties")
                .run(args);
        return CTX;
    }

    public static ConfigurableApplicationContext startServer2(String ...args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ConfigurableApplicationContext ctx = builder.sources(ServerStarter.class)
                .properties("spring.config.location=classpath:/server2/application.properties")
                .run(args);
        return ctx;
    }


    public static ConfigurableApplicationContext startServer3(String ...args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ConfigurableApplicationContext ctx = builder.sources(ServerStarter.class)
                .properties("spring.config.location=classpath:/server3/application.properties")
                .run(args);
        return ctx;
    }

    public static ConfigurableApplicationContext startServer4(String ...args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ConfigurableApplicationContext ctx = builder.sources(ServerStarter.class)
                .properties("spring.config.location=classpath:/server4/application.properties")
                .run(args);
        return ctx;
    }

    public static String getServerPort(ConfigurableApplicationContext ctx) {
        return ctx.getEnvironment().getProperty("local.server.port");
    }


    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated().and().csrf().disable();
        }

    }

}
