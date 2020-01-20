package eu.ecodex.utils.spring.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


public class PathConverter implements Converter<String, Path> {

    @Override
    public Path convert(String s) {

        if (s.startsWith("file://")) {
            s = s.substring("file://".length());
        }
        if (s.startsWith("file:")) {
            s = s.substring("file:".length());
        }
        return Paths.get(s);
    }
}
