package com.loadbalancer;

import com.loadbalancer.config.Config;
import com.loadbalancer.enums.LoadBalancingAlgorithm;
import com.loadbalancer.models.InstanceProvider;
import com.loadbalancer.strategies.RandomLoadBalancer;
import com.loadbalancer.models.impl.InstanceProviderImpl;
import com.loadbalancer.service.LoadBalancerServer;
import com.loadbalancer.models.LoadBalancerProvidersHandler;
import com.loadbalancer.strategies.RoundRobinLoadBalancer;
import com.loadbalancer.utils.InstancesInitializer;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    private static int checkIfValidInteger(String value) {
        int parsedValue = 0;
        try {
            parsedValue = Integer.parseInt(value);
            if (parsedValue < 0 ) {
                System.out.println(value + " not a positive number");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println(value + " not a number");
            System.exit(1);
        }
        return parsedValue;
    }

    public static void main(String[] args) throws IOException {
        LoadBalancingAlgorithm algo = LoadBalancingAlgorithm.RoundRobin;
        int instances = Config.MAX_PROVIDERS;
        int healthcheckSeconds = Config.DEFAULT_HEALTHCHECK_SECONDS;
        if (args.length >= 2) {
            if (args[0].equals("--algo")) {
                try {
                    algo = LoadBalancingAlgorithm.valueOf(args[1]);
                } catch (IllegalArgumentException e){
                    System.out.println(args[1] + " " + " algorithm not recognized");
                    System.exit(1);
                }
            }
            if (args.length >= 4 && args[2].equals("--instances")) {
                instances = checkIfValidInteger(args[3]);
                if (instances > Config.MAX_PROVIDERS) {
                    System.out.println(args[3] + " bigger than the maximum size which is " + Config.MAX_PROVIDERS);
                    System.exit(1);
                }
            }
            if (args.length >= 6 && args[4].equals("--healthcheck_seconds")) {
                healthcheckSeconds = checkIfValidInteger(args[5]);
            }
            if (args.length >= 8 && args[6].equals("--healthcheck_failure_chance")) {
                try {
                    float healthcheckFailureChance = Float.parseFloat(args[7]);
                    if (healthcheckFailureChance < 0 ) {
                        System.out.println(args[7] + " not a positive number");
                        System.exit(1);
                    }
                    Config.HEALTHCHECK_FAILURE_CHANCE = healthcheckFailureChance;
                } catch (NumberFormatException e) {
                    System.out.println(args[7] + " not a number");
                    System.exit(1);
                }

            }

            if (args.length >= 10 && args[8].equals("--service_simulate_delay")) {
                int threadDelay = checkIfValidInteger(args[9]);
                Config.THREAD_DELAY = threadDelay;
            }
        }

        List<InstanceProvider> providers = InstancesInitializer.initialize(instances);

        LoadBalancerServer balancer = new LoadBalancerServer(algo, providers,
                Config.DEFAULT_HEALTHCHECK_SECONDS, healthcheckSeconds, Config.LOAD_BALANCER_PORT);

        new Thread(balancer).start();
    }
}
