package com.dliu.micrometer.akka.sample;

import com.dliu.micrometer.akka.sample.http.PrometheusEndpoint;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.stream.Materializer;
import io.kontainers.micrometer.akka.AkkaMetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

public class Main2 {
    public static void main(String[] args) {
        PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, CollectorRegistry.defaultRegistry, Clock.SYSTEM);
        AkkaMetricRegistry.setRegistry(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        ActorSystem system = ActorSystem.create("system");
        Materializer materializer = Materializer.createMaterializer(system);
        Http.get(system).bindAndHandle(new PrometheusEndpoint(CollectorRegistry.defaultRegistry).route().flow(system, materializer),
                                       ConnectHttp.toHost("0.0.0.0", 12345),
                                       materializer);
    }
}
