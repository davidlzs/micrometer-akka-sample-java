package com.dliu.micrometer.akka.sample.http;

import java.util.concurrent.ExecutionException;

import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;

public class WebServer extends HttpApp {
    public void start() {
        try {
            startServer("0.0.0.0", 12345);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected Route routes() {
        return new PrometheusService().route();
    }
}
