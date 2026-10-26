package com.algonix.server.config;

import com.algonix.server.entity.PlatformType;
import com.algonix.server.service.CodingPlatformService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CodingPlatformFactory {

    private final Map<PlatformType, CodingPlatformService> services;

    public CodingPlatformFactory(List<CodingPlatformService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(
                        CodingPlatformService::getPlatform,
                        Function.identity()
                ));
    }

    public CodingPlatformService getService(PlatformType platform) {
        return services.get(platform);
    }
}
