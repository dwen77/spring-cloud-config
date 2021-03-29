package com.cloud.config.servicex.api;

import com.cloud.config.property.beans.BlackListProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceXController {

    private final BlackListProperties blackListProps;

    public ServiceXController(BlackListProperties blackListProps) {
        this.blackListProps = blackListProps;
    }

    @GetMapping(
            value = "/whoami/{username}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String whoami(@PathVariable("username") String username) {
        if (blackListProps.getUsernames().contains(username)) {
            return String.format("Hello! You're %s and you are blocked", username);
        }
        return String.format("Hello! You're %s\n", username);
    }
}
