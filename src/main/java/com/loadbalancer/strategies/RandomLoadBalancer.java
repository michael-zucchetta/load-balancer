package com.loadbalancer.strategies;

import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.impl.LoadBalancerProvidersHandlerAbstract;

import java.util.*;

// Step 3 – Random invocation
public class RandomLoadBalancer extends LoadBalancerProvidersHandlerAbstract<List<InstanceProvider>> {
    private Random random = new Random();

    public RandomLoadBalancer(int maxSize, int healthCheckSeconds) {
        super(new ArrayList<>(), new ArrayList<>(), maxSize, healthCheckSeconds);
    }

    @Override
    public InstanceProvider get() {
        int selectedProvider = (random.nextInt() & Integer.MAX_VALUE) % this.providers.size();
        return this.providers.get(selectedProvider);
    }
}
