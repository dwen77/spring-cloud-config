package com.cloud.config.servicex.api;

import com.cloud.config.property.beans.BlackListProperties;

public class BlackListChecker {
    public String check(String username) {
        BlackListProperties blackListProps = SpringBeanLoader.getBean(BlackListProperties.class);
        if (blackListProps.getUsernames().contains(username)) {
            return String.format("Hello! You're %s and you are blocked", username);
        }
        return String.format("Hello! You're %s\n", username);
    }
}
