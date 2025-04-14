package how.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class DemoApp {
    private static final String INSTRUMENTATION_NAME = "OTelDemoApp";

    public static void main(String[] args) {
        // Initialize OpenTelemetry (set up exporters to console)
        OpenTelemetry openTelemetry = OTelInitializer.initOpenTelemetry();

        // Get a Tracer and Meter from the OpenTelemetry API
        Tracer tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME);
        Meter meter = openTelemetry.getMeter(INSTRUMENTATION_NAME);

        // Create a Counter instrument for metrics
        LongCounter itemsProcessedCounter = meter
                .counterBuilder("processed_items")
                .setDescription("Number of items processed")
                .setUnit("1")
                .build();

        // Start a span for the batch process
        Span batchSpan = tracer.spanBuilder("processBatch").startSpan();
        try (Scope batchScope = batchSpan.makeCurrent()) {
            // Simulate processing 5 items, each item will have its own span
            for (int i = 1; i <= 5; i++) {
                Span itemSpan = tracer.spanBuilder("processItem").startSpan();
                try (Scope itemScope = itemSpan.makeCurrent()) {
                    // Simulate some work for the item
                    System.out.println("Processing item " + i);
                    // Record a metric for the processed item
                    itemsProcessedCounter.add(1);
                } finally {
                    itemSpan.end();  // end the item span
                }
            }
        } finally {
            batchSpan.end();  // end the batch span
        }

        // Wait briefly to ensure the last metric is exported
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {

        }
    }
}