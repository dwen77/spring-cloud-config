package com.cloud.config.servicex.api;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringBeanLoader {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
