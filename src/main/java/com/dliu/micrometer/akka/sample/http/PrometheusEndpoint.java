package com.dliu.micrometer.akka.sample.http;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import akka.http.javadsl.model.HttpCharsets;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.MediaType;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

public class PrometheusEndpoint extends AllDirectives  {
    final MediaType.WithFixedCharset prometheusTextType =
            MediaTypes.customWithFixedCharset("text", "plain",
                                              HttpCharsets.UTF_8,
                                              Collections.singletonMap("version", "0.0.4"),
                                              false);

    private final CollectorRegistry registry;

    public PrometheusEndpoint(CollectorRegistry registry) {
        this.registry = registry;
    }

    private String renderMetrics(CollectorRegistry registry) {
        StringWriter writer = new StringWriter();
        try {
            TextFormat.write004(writer, registry.metricFamilySamples());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public Route route() {
        return path("metrics", () -> {
            String content = renderMetrics(registry);
            return complete(HttpEntities.create(prometheusTextType.toContentType(), content));
        });
    }
}
