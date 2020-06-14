package com.loadbalancer.service;

import com.loadbalancer.config.Config;
import com.loadbalancer.enums.LoadBalancingAlgorithm;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.utils.InstancesInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadBalancerServerSpec {

    static LoadBalancerServer server;
    static Thread threadServer;
    static int port = 7779;

    @BeforeAll
    public static void initServer() {
        try {
            Config.HEALTHCHECK_FAILURE_CHANCE = 1f;
            int prevServiceInitialPort = Config.DEFAULT_SERVICE_INITIAL_PORT;
            Config.DEFAULT_SERVICE_INITIAL_PORT = 12000;
            List<InstanceProvider> instances = InstancesInitializer.initialize(5);
            Config.DEFAULT_SERVICE_INITIAL_PORT = prevServiceInitialPort;
            server = new LoadBalancerServer(LoadBalancingAlgorithm.RoundRobin, instances, 5, 5, port);
            threadServer = new Thread(server);
            threadServer.start();
        } catch (IOException e) {

        }
    }

    @Test
    public void checkDifferentCalls() throws IOException {

        String responses[] = new String[5];
        for (int i = 0; i < 5; ++i) {
            Socket call = new Socket("localhost", port);
            byte buffer[] = call.getInputStream().readAllBytes();
            responses[i] = new String(buffer);
        }
        for (int i = 0; i < 10; ++ i) {
            Socket call = new Socket("localhost", port);
            byte buffer[] = call.getInputStream().readAllBytes();
            String response = new String(buffer);
            assertEquals(responses[i % 5], response);
        }
        for (int i = 1; i < responses.length; ++i) {
            System.out.println(responses[i]);
            assertNotEquals(responses[i - 1], responses[i]);
        }
    }

    @Test
    public void saturateServerCalls() throws IOException, InterruptedException {

        Config.THREAD_DELAY = 10000;
        for (int i = 0; i < 10; ++i) {
            Socket call = new Socket("localhost", port);

        }
        assertEquals(server.currentCallsSize.get(), server.strategy.size() + 1);
    }
}
