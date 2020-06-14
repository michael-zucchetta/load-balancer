package com.loadbalancer.models.impl;

import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.LoadBalancerProvidersHandler;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class LoadBalancerProvidersHandlerAbstract<T extends Collection<InstanceProvider>>
        implements LoadBalancerProvidersHandler {

    protected T providers;
    protected T unavailableProviders;
    private final int maxSize;
    private final int healthCheckSeconds;
    private final ScheduledExecutorService healthCheckScheduler;

    public void setUpHealthchecks() {
        // Step 6 – Heart beat checker
        healthCheckScheduler.scheduleAtFixedRate(() -> {
            // if the check fails
            List<InstanceProvider> failed = providers.stream().filter((provider) ->
                    !provider.check()).collect(Collectors.toList());

            List<InstanceProvider> toBeRestored = unavailableProviders.stream().filter((provider) ->
                    (provider.check() && provider.readyToBeBack())).collect(Collectors.toList());
            failed.forEach((provider) -> {
                removeProvider(provider.get());
                System.out.println(String.format("Instance %s failed check", provider.get()));
                unavailableProviders.add(provider);
            });
            if (unavailableProviders.size() > 0) {
                System.out.println(String.format("unavailable services %s", unavailableProviders.size()));
            }
            toBeRestored.forEach((provider) -> {
                // Step 7 – ​Improving Heart beat checker
                if (provider.check() && provider.readyToBeBack()) {
                    System.out.println(String.format("Instance %s restored", provider.get()));

                    unavailableProviders.remove(provider);
                    addProvider(provider);
                }
            });
        }, healthCheckSeconds, healthCheckSeconds, TimeUnit.SECONDS);
    }

    public LoadBalancerProvidersHandlerAbstract(T providers, T unavailableProviders, int maxSize, int healthCheckSeconds) {
        this.providers = providers;
        this.unavailableProviders = unavailableProviders;
        this.maxSize = maxSize;
        this.healthCheckSeconds = healthCheckSeconds;
        this.healthCheckScheduler = Executors.newSingleThreadScheduledExecutor();
        setUpHealthchecks();
    }

    public boolean addProvider(InstanceProvider provider) {
        if (providers.size() < this.maxSize) {
            this.providers.add(provider);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeProvider(String identifier) {
        Optional<InstanceProvider> providerToBeRemovedOpt =
                this.providers.stream().filter((element) -> element.get().equals(identifier)).findFirst();
        if (providerToBeRemovedOpt.isEmpty()) {
            return false;
        } else {
            this.providers.remove(providerToBeRemovedOpt.get());
            return true;
        }
    }

    @Override
    public int size() {
        return this.providers.size();
    }
}
