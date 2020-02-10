package com.dliu.micrometer.akka.sample;

import com.dliu.micrometer.akka.sample.http.WebServer;

import io.kontainers.micrometer.akka.AkkaMetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

public class Main {
    public static void main(String[] args) {
        PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM);
        AkkaMetricRegistry.setRegistry(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        new WebServer()
                .start();
    }
}
