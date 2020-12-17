package io.quarkiverse.micrometer.registry.azuremonitor.deployment;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.test.QuarkusUnitTest;

public class AzureMonitorEnabledTest {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.azuremonitor.AzureMonitorMeterRegistry";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.azuremonitor.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.azuremonitor.instrumentation-key", "TEST")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(AzureMonitorRegistryProcessor.REGISTRY_CLASS));

    @Inject
    MeterRegistry registry;

    @Test
    public void testMeterRegistryPresent() {
        // AzureMonitor is enabled (alone, all others disabled)
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(),
                "There should be a sub-registry: " + subRegistries);
        Assertions.assertEquals(
                REGISTRY_CLASS_NAME, subRegistries.iterator().next().getClass().getName(),
                "Should be AzureMonitorMeterRegistry");
    }
}
