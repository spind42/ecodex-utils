package eu.ecodex.utils.spring.starter;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class SpringBootWarOnTomcatStarter extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootWarOnTomcatStarter.class);

    public static final String CATALINA_HOME = "catalina.home";
    private String servletPath;


    public void run(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        Properties springProperties = new Properties();
        configureApplicationContext(builder, springProperties);
        builder.sources(getSources());
        ConfigurableApplicationContext run = builder.run(args);
    }


    private Path resolveCatalinaHome() {
        String catalinaHome = System.getProperty(CATALINA_HOME);
        Path currentRelativePath;
        if (catalinaHome != null) {
            currentRelativePath = Paths.get(catalinaHome);
            return currentRelativePath;
        }
        return null;
    }


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String servletPath = servletContext.getContextPath();
        servletPath = servletPath.substring(1); //remove leading / from serlvetPath: see javadoc of getContextPath for details
        this.servletPath = servletPath;
        super.onStartup(servletContext);
    }


    @Override
    protected final SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        Properties springProperties = new Properties();
        List<String> springConfigLocations = new ArrayList<>();

        springConfigLocations.add("classpath:/config/");
        springConfigLocations.add(String.format("classpath:/config/%s/", getApplicationConfigLocationName())); //look at relative directory config/<context>
        if (resolveCatalinaHome() != null) {
            Path catalinaHomePath = resolveCatalinaHome();
            String catalinaPathConf = String.format("file:%s/conf/%s/", catalinaHomePath.toAbsolutePath(), getApplicationConfigLocationName());
            LOGGER.info("CatalinHome is set - adding [{}] to spring.config.location", catalinaPathConf);
            springConfigLocations.add(catalinaPathConf);

            String catalinaPathConfig = String.format("file:%s/config/%s/", catalinaHomePath.toAbsolutePath(), getApplicationConfigLocationName());
            LOGGER.info("CatalinHome is set - adding [{}] to spring.config.location", catalinaPathConfig);
            springConfigLocations.add(catalinaPathConfig);
        }

        if (System.getProperty("spring.config.location") == null) {
            String configLocations = springConfigLocations.stream().collect(Collectors.joining(","));
            LOGGER.info("SystemProperty spring.config.location is not set - setting as spring.config.location: [{}]", configLocations);
            springProperties.setProperty("spring.config.location", configLocations);
        } else {
            LOGGER.info("SystemProperty spring.config.location is set - using spring.config.location={}", System.getProperty("spring.config.location"));
        }

        springProperties.setProperty("spring.config.name", getConfigName());

        configureApplicationContext(application, springProperties);

        LOGGER.info("Using properties [{}] in spring application", springProperties);
        application.properties(springProperties);
        application.sources(getSources());
        return application;
    }

    protected void configureApplicationContext(SpringApplicationBuilder application, Properties springProperties) {
    }

    /**
     * This method returnes the directory name of the directory
     * containing the spring properties
     *
     * By default if started within a servletContext, the servletContextName is used
     * eg. if the application is deployed on a tomcat under /app123
     * then this method will return app123
     *
     *
     * @return the returned value will be used to configure spring.config.location
     *
     */
    protected String getApplicationConfigLocationName() {
        if (this.servletPath == null) {
            return "";
        } else {
            return this.servletPath;
        }
    }

    /**
     * is used to set the spring property spring.config.name
     *  the default value is application so spring boot will look for a application.properties file
     *  for loading the properties (or application.yml if yml loading is available)
     *
     *  can be overwritten to
     *  {@code
     *      protected String getConfigName() {
     *          return "foobar";
     *      }
     *  }
     *
     *  then spring.config.name will be set to foobar and spring boot will look for foobar.properties
     *
     *
     * @return the returned value will be set as spring.config.name
     *
     */
    protected String getConfigName() {
        return "application";
    }

    /**
     * Must be overwritten
     *
     * @return the returned classes are used as application source
     * for spring
     */
    protected abstract Class<?>[] getSources();


}
