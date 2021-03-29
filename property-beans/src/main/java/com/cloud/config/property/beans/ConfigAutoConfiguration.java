package com.cloud.config.property.beans;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BlackListProperties.class)
public class ConfigAutoConfiguration {
}
