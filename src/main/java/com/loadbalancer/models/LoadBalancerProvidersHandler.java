package com.loadbalancer.models;

import java.util.Collection;
import java.util.UUID;

public interface LoadBalancerProvidersHandler<T extends Collection<InstanceProvider>> {
    InstanceProvider get();
    // Step 5 – Manual node exclusion / inclusion
    boolean addProvider(InstanceProvider provider);
    boolean removeProvider(String identifier);
    int size();
}
