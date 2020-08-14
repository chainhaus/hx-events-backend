package com.fidecent.fbn.hx.configs;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class JacksonConfig {
    private static final String TIME_FORMAT = "h:mm a";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT).localizedBy(Locale.US);
            builder.deserializers(new LocalTimeDeserializer(timeFormatter));
            builder.serializers(new LocalTimeSerializer(timeFormatter));
        };
    }
}
