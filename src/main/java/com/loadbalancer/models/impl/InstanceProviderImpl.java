package com.loadbalancer.models.impl;

import com.loadbalancer.config.Config;
import com.loadbalancer.models.InstanceProvider;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class InstanceProviderImpl extends ServerSocket implements InstanceProvider {
    private UUID identifier;
    private int consecutiveSuccessChecks = 0;
    private String CRLF = "\r\n";
    private String standardResponse = "HTTP/1.1 200 OK" + CRLF +
        "Content-Type: text-plain" + CRLF +
            "Content-Length: 46" + CRLF + CRLF
            + " Ok for ";
    private int port;

    public InstanceProviderImpl(int port) throws IOException {
        super(port);
        this.port = port;
        this.identifier = UUID.randomUUID();
    }

    public String get() {
        return identifier.toString();
    }

    public boolean readyToBeBack() {
        return consecutiveSuccessChecks >= 2;
    }

    public boolean check() {
        if (new Random().nextFloat() <= Config.HEALTHCHECK_FAILURE_CHANCE) {
            consecutiveSuccessChecks++;
            return true;
        } else {
            consecutiveSuccessChecks = 0;
            return false;
        }
    }

    @Override
    public int getPort() {
        return this.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = this.accept();

                BufferedOutputStream oos = new BufferedOutputStream(socket.getOutputStream());
                String finalResponse = standardResponse + this.get() + CRLF;
                oos.write(finalResponse.getBytes("UTF-8"));
                oos.close();
                socket.close();
            } catch (IOException e) {

            }
        }
    }

}
