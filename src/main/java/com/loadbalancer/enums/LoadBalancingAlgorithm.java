package com.loadbalancer.enums;

public enum LoadBalancingAlgorithm {
    Random("Random"),
    RoundRobin("RoundRobin");

    String name;

    LoadBalancingAlgorithm(String name) {
        this.name = name;
    }
}
