package com.loadbalancer.models.impl;

import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.LoadBalancerProvidersHandler;
import com.loadbalancer.models.impl.InstanceProviderImpl;
import com.loadbalancer.models.impl.LoadBalancerProvidersHandlerAbstract;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LoadBalancerProvidersHandlerSpec {

    LoadBalancerProvidersHandler handler = new LoadBalancerProvidersHandlerAbstract(new ArrayList<>(), new ArrayList<>(), 5, 1) {
        @Override
        public InstanceProvider get() {
            return null;
        }
    };

    @Test
    public void testProvider() throws IOException {
        InstanceProvider provider = new InstanceProviderImpl(9001);
        handler.addProvider(provider);
        assertEquals(handler.size(), 1);
    }

    @Test
    public void removeProvider() {
        assertFalse(handler.removeProvider(UUID.randomUUID().toString()));
    }

    @Test
    public void checkFailingInstance() throws IOException, InterruptedException {
        InstanceProvider provider = new InstanceProviderImpl(9002);
        handler.addProvider(provider);
        assertEquals(handler.size(), 1);
        float prevHealthcheckFailureChance = Config.HEALTHCHECK_FAILURE_CHANCE;
        Config.HEALTHCHECK_FAILURE_CHANCE = 0.0f;
        Thread.sleep(1200);
        assertEquals(handler.size(), 0);
        Config.HEALTHCHECK_FAILURE_CHANCE = prevHealthcheckFailureChance;
    }

    @Test
    public void surpassMaximumInstances()  throws IOException {
        InstanceProvider provider = new InstanceProviderImpl(9003);
        assertTrue(handler.addProvider(provider));
        assertTrue(handler.addProvider(provider));
        assertTrue(handler.addProvider(provider));
        assertTrue(handler.addProvider(provider));
        assertTrue(handler.addProvider(provider));
        boolean surpassLimit = handler.addProvider(provider);
        assertFalse(surpassLimit);
    }

}
