package com.loadbalancer.strategies;

import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;

import com.loadbalancer.utils.InstancesInitializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinLoadBalancerSpec {
    static List<InstanceProvider> instances;
    static {
        Config.DEFAULT_SERVICE_INITIAL_PORT = 9999;
        instances = InstancesInitializer.initialize(5);
    }

    @Test
    public void verifyCorrectOrder() {
        RoundRobinLoadBalancer balancer = new RoundRobinLoadBalancer(2, 5);
        balancer.addProvider(instances.get(0));
        balancer.addProvider(instances.get(1));

        assertEquals(balancer.get().get(), instances.get(0).get());
        assertEquals(balancer.get().get(), instances.get(1).get());
        assertEquals(balancer.get().get(), instances.get(0).get());
    }

    @Test
    public void verifyCorrectOrderSameInstance() {
        RoundRobinLoadBalancer balancer = new RoundRobinLoadBalancer(1, 5);
        balancer.addProvider(instances.get(0));

        assertEquals(balancer.get().get(), instances.get(0).get());
        assertEquals(balancer.get().get(), instances.get(0).get());
        assertEquals(balancer.get().get(), instances.get(0).get());
    }
}
