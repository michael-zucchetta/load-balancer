package com.loadbalancer.utils;

import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.impl.InstanceProviderImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InstancesInitializer {
    public static List<InstanceProvider> initialize(int instancesSize) {
        return IntStream.range(0, instancesSize).mapToObj( (instanceIndex) -> {
            try {
                InstanceProvider provider = new InstanceProviderImpl(Config.DEFAULT_SERVICE_INITIAL_PORT + instanceIndex);
                new Thread(provider).start();
                return provider;
            } catch (IOException e) {
                System.out.println("Something went wrong during service initialization with port " + Config.DEFAULT_SERVICE_INITIAL_PORT + instanceIndex);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
