package com.loadbalancer.config;

public class Config {

    public static int MAX_PROVIDERS = 10;

    public static float HEALTHCHECK_FAILURE_CHANCE = 0.85f;

    public static int LOAD_BALANCER_PORT = 7777;

    public static int DEFAULT_SERVICE_INITIAL_PORT = 10800;

    public static int DEFAULT_HEALTHCHECK_SECONDS = 30;

    public static int THREAD_DELAY = 0;
}
