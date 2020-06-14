package com.loadbalancer.strategies;

import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.utils.InstancesInitializer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RandomLoadBalancerSpec {
    static List<InstanceProvider> instances;
    static {
        Config.DEFAULT_SERVICE_INITIAL_PORT = 10999;
        instances = InstancesInitializer.initialize(5);
    }

    @Test
    public void verifyCorrectOrder() {


        RandomLoadBalancer balancer = new RandomLoadBalancer(4, 5);
        balancer.addProvider(instances.get(0));
        balancer.addProvider(instances.get(1));
        balancer.addProvider(instances.get(2));
        balancer.addProvider(instances.get(3));

        Map<String, Integer> count = new HashMap<>(1000);
        for (int i = 0; i < 1000; ++i) {
            String id = balancer.get().get();
            if (!count.containsKey(id)) {
                count.put(id, 1);
            } else {
                count.put(id, count.get(id) + 1);
            }
        }


        List<Integer> countCalls = count.keySet().stream().map((key) -> count.get(key)).collect(Collectors.toList());

        int callsService1 = countCalls.get(0);
        int callsService2 = countCalls.get(1);
        int callsService3 = countCalls.get(2);
        int callsService4 = countCalls.get(3);

        boolean check1 = callsService1 == callsService2;
        boolean check2 = callsService2 == callsService3;
        boolean check3 = callsService3 == callsService4;
        boolean check4 = callsService4 == callsService1;
        boolean isAllTrue = Arrays.asList(new Boolean[] { check1, check2, check3, check4 }).stream().allMatch(val -> val);

        // shouldn't be equal
        assertFalse(isAllTrue);
    }

}
