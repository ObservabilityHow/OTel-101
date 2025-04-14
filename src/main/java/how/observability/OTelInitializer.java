package how.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import java.time.Duration;

public class OTelInitializer {

    // Configure OpenTelemetry SDK with console (logging) exporters
    public static OpenTelemetry initOpenTelemetry() {
        // 1. Configure TracerProvider with LoggingSpanExporter (for traces)
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                .build();

        // 2. Configure MeterProvider with LoggingMetricExporter (for metrics)
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(LoggingMetricExporter.create())
                        .setInterval(Duration.ofMillis(1000)) // export metrics every 1s
                        .build()
                )
                .build();

        // 3. Build OpenTelemetry SDK instance with our providers and make it global
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setMeterProvider(meterProvider)
                .buildAndRegisterGlobal();
        return openTelemetry;
    }
}
