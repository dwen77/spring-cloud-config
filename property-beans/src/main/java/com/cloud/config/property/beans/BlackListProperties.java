package com.cloud.config.property.beans;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ConfigurationProperties("config")
@Validated
@Data
public class BlackListProperties {

    @NotEmpty
    private List<String> usernames;
}
