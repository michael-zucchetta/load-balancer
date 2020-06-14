package com.loadbalancer.service;


import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RequestWorker implements Runnable {

    private Socket clientSocket = null;
    private InstanceProvider currentInstance;

    public RequestWorker(Socket clientSocket, InstanceProvider currentInstance) {
        this.clientSocket = clientSocket;
        this.currentInstance = currentInstance;
    }

    public void run() {
        try {
            System.out.println(this.currentInstance.get());

            Socket responseSocket = new Socket("localhost", currentInstance.getPort());

            byte buffer[] = responseSocket.getInputStream().readAllBytes();

            OutputStream out = clientSocket.getOutputStream();
            out.write(buffer);
            out.close();
            responseSocket.close();
            Thread.sleep(Config.THREAD_DELAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException f) {
            f.printStackTrace();
        }
    }
}