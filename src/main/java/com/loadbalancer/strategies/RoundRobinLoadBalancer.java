package com.loadbalancer.strategies;

import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.impl.LoadBalancerProvidersHandlerAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//  Step 4 – Round Robin invocation
public class RoundRobinLoadBalancer extends LoadBalancerProvidersHandlerAbstract<List<InstanceProvider>> {

    private final AtomicInteger currentInstance = new AtomicInteger(-1);

    public RoundRobinLoadBalancer(int maxSize, int healthCheckSeconds) {
        super(new ArrayList<>(), new ArrayList<>(), maxSize, healthCheckSeconds);
    }

    @Override
    public InstanceProvider get() {
        int currentProvider = currentInstance.incrementAndGet() % this.providers.size();
        return this.providers.get(currentProvider);
    }
}
