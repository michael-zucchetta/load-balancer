package com.loadbalancer.models;

import java.util.UUID;

/*
  Step 1 – Generate provider
 */
public interface InstanceProvider extends Runnable {
    String get();
    boolean check();
    boolean readyToBeBack();
    int getPort();
}
