package com.cloud.config.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.config.monitor.GithubPropertyPathNotificationExtractor;
import org.springframework.cloud.config.monitor.PropertyPathNotification;
import org.springframework.cloud.config.monitor.PropertyPathNotificationExtractor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "${spring.cloud.config.monitor.endpoint.path:}/my-monitor")
public class MyPropertyPathEndpoint implements ApplicationEventPublisherAware {

    private static Log log = LogFactory.getLog(MyPropertyPathEndpoint.class);
    private final PropertyPathNotificationExtractor extractor;

    private ApplicationEventPublisher applicationEventPublisher;

    private String busId;

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public MyPropertyPathEndpoint(BusProperties busProperties) {
        this.extractor = new GithubPropertyPathNotificationExtractor();
        this.busId = busProperties.getId();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Set<String> notifyByPath(@RequestHeader HttpHeaders headers,
                                    @RequestBody Map<String, Object> request) {
        PropertyPathNotification notification = this.extractor.extract(headers, request);
        if (notification != null) {

            Set<String> services = new LinkedHashSet<>();

            for (String path : notification.getPaths()) {
                services.addAll(guessServiceName(path));
            }
            if (this.applicationEventPublisher != null) {
                for (String service : services) {
                    log.info("Refresh for: " + service);
                    this.applicationEventPublisher.publishEvent(
                            new RefreshRemoteApplicationEvent(this, this.busId, service));
                }
                return services;
            }

        }
        return Collections.emptySet();
    }

    private Set<String> guessServiceName(String path) {
        Set<String> services = new LinkedHashSet<>();
        if (path != null) {
            String serviceName = getServiceNameFromPath(path);
            String stem = StringUtils.stripFilenameExtension(
                    StringUtils.getFilename(StringUtils.cleanPath(path)));
            // TODO: correlate with service registry
            int index = stem.indexOf("-");
            while (index >= 0) {
                String name = stem.substring(0, index);
                String profile = stem.substring(index + 1);
                if ("application".equals(name)) {
                    services.add(serviceName + ":" + profile);
                }
                else if (!name.startsWith("application")) {
                    services.add(name + ":" + profile);
                }
                index = stem.indexOf("-", index + 1);
            }
            String name = stem;
            if ("application".equals(name)) {
                services.add(serviceName);
            }
            else if (!name.startsWith("application")) {
                services.add(name);
            }
        }
        return services;
    }

    private String getServiceNameFromPath(String path) {
        int pathSeparatorIdx = path.indexOf("/");
        if (pathSeparatorIdx == -1) {
            return "*";
        }
        int configFileIdx = path.substring(0, pathSeparatorIdx).indexOf("config-file");
        if (configFileIdx == -1) {
            return "*";
        }
        return path.substring(0, configFileIdx - 1);
    }
}
