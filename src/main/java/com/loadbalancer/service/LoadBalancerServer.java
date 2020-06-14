package com.loadbalancer.service;

import com.loadbalancer.enums.LoadBalancingAlgorithm;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.models.LoadBalancerProvidersHandler;
import com.loadbalancer.strategies.RandomLoadBalancer;
import com.loadbalancer.strategies.RoundRobinLoadBalancer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancerServer implements Runnable {

    protected LoadBalancerProvidersHandler strategy;
    protected AtomicInteger currentCallsSize;
    private ServerSocket socket;

    public LoadBalancerServer(LoadBalancingAlgorithm algorithm,
                              List<InstanceProvider> instances,
                              int instancesMaxSize,
                              int healthcheckSeconds, int port) throws IOException {

        // Step 2 – Register a list of providers
        if (algorithm == LoadBalancingAlgorithm.RoundRobin) {
            this.strategy = new RoundRobinLoadBalancer(instancesMaxSize, healthcheckSeconds);
        } else {
            this.strategy = new RandomLoadBalancer(instancesMaxSize, healthcheckSeconds);
        }
        instances.forEach(strategy::addProvider);
        this.socket = new ServerSocket(port);
        this.currentCallsSize = new AtomicInteger(0);
    }

    @Override
    public void run() {

        while (true) {

            try {
                Socket loadBalancerSocket = this.socket.accept();
                // Step 8 – Cluster Capacity Limit
                if (currentCallsSize.get() > this.strategy.size()) {
                    System.out.println("Too many requests");
                    loadBalancerSocket.close();
                } else if (this.strategy.size() == 0 ) {
                    System.out.println("No instances currently available");
                    loadBalancerSocket.close();
                } else {

                    currentCallsSize.incrementAndGet();
                    InstanceProvider current = this.strategy.get();

                    var worker = new RequestWorker(loadBalancerSocket, current);
                    CompletableFuture.runAsync(worker).thenRun(() -> {
                        int activeCalls = currentCallsSize.decrementAndGet();
                        System.out.println("Released " + activeCalls);
                    }).thenRun(() -> {
                        try {
                            loadBalancerSocket.close();
                        } catch (IOException e) {
                            System.err.println("Socket already closed");
                        }
                    });
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
