package com.aitherapist.aitherapist.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * BotProperties - keep name, and token
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    private String name;
    private String token;
}
