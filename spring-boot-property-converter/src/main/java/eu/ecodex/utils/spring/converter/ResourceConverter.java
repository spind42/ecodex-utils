package eu.ecodex.utils.spring.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class ResourceConverter implements Converter<String, Resource> {

    @Override
    public Resource convert(String source) {
        if (source.startsWith("classpath:")) {
            return new ClassPathResource(source.substring("classpath:".length()));
        }
        if (source.startsWith("file://")) {
            return new FileSystemResource(source.substring("file://".length()));
        }
        if (source.startsWith("file:")) {
            return new FileSystemResource(source.substring("file:".length()));
        }
        try {
            return new UrlResource(source);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
