package com.dliu.micrometer.akka.sample.http;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.http.javadsl.model.HttpCharsets;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaType;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.IOResult;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.StreamConverters;
import akka.util.ByteString;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

public class PrometheusService extends AllDirectives {

    final MediaType.WithFixedCharset prometheusTextType =
            MediaTypes.customWithFixedCharset("text", "plain",
                                              HttpCharsets.UTF_8,
                                              Collections.singletonMap("version", "0.0.4"),
                                              false);

    public Route route() {
        return path("metrics", () -> {
            PipedInputStream in = new PipedInputStream();
            Source<ByteString, CompletionStage<IOResult>> byteSource = StreamConverters.fromInputStream(() -> in);
            try {
                OutputStreamWriter out = new OutputStreamWriter(new PipedOutputStream(in), HttpCharsets.UTF_8.value());
                CompletableFuture.runAsync(() -> {
                    try {
                        try {
                            TextFormat.write004(out, CollectorRegistry.defaultRegistry.metricFamilySamples());
                            out.flush();
                        } finally {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return complete(HttpResponse.create()
                                    .withEntity(HttpEntities.create(prometheusTextType.toContentType(), byteSource)));
        });
    }
}
