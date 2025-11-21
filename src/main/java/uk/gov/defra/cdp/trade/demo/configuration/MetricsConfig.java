package uk.gov.defra.cdp.trade.demo.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Micrometer metrics.
 *
 * <p>Micrometer provides standard JVM, HTTP, and database metrics via Spring Boot Actuator. Custom
 * business metrics use AWS EMF (see EmfMetricsConfig and MetricsService).
 *
 * <p>ARCHITECTURE: - Standard metrics (JVM, HTTP, DB): Micrometer via Spring Boot Actuator - Custom
 * business metrics: AWS Embedded Metrics Format (EMF)
 *
 * <p>This configuration only provides a fallback SimpleMeterRegistry when metrics are disabled.
 * When enabled, Spring Boot Actuator auto-configures appropriate registries.
 */
@Slf4j
@Configuration
public class MetricsConfig {

  private static final String[] IGNORED_METRICS = {"jvm.compilation.time",
      "tasks.scheduled.execution.active", "jvm.memory.used.bytes", "jvm.memory.committed.bytes", "jvm.memory.max.bytes",
      "jvm.gc.max.data.size", "jvm.gc.live.data.size", "jvm.gc.memory.promoted", "jvm.gc.memory.allocated",
      "jvm.threads.live", "jvm.threads.daemon", "jvm.threads.peak", "jvm.threads.states", "jvm.threads.classes",
      "jvm.threads.started", "jvm.threads.deadlocked", "jvm.threads.deadlocked.monitor", "jvm.threads.state.new",
      "jvm.threads.state.new.count", "jvm.threads.state.runnable", "jvm.threads.state.runnable.count",
      "jvm.threads.state.blocked", "jvm.threads.state.blocked.count", "jvm.threads.state.waiting"};

  private static final String[] ACCEPTED_METRICS = {"jvm.memory.used", "jvm.memory.committed",
      "jvm.memory.max"};

  private static final String[] PROCESS_METRICS = {"process.cpu.usage"};

  private static final String[] HTTP_METRICS = {"http.server.requests"};

  private static final String[] HIKARI_METRICS = {"hikaricp.connections.max", "hikaricp.connections.active",
      "hikaricp.connections", "hikaricp.connections.acquire", "hikaricp.connections.usage"};

  private static final String[] LOGBACK_METRICS = {"logback.events", "logback.events.total"};

  private static final String[] TOMCAT_METRICS = {"tomcat.sessions.active.current", "tomcat.sessions.active.max",
      "tomcat.sessions.created", "tomcat.sessions.rejected", "tomcat.sessions.expired", "tomcat.sessions.accessed"};

  private static final String[] EXECUTOR_METRICS = {"executor.completed", "executor.queued", "executor.active",
      "executor.pool.size", "executor.pool.active", "executor.pool.largest"};

  private static final String[] APPLICATION_METRICS = {"application.started.timestamp", "application.uptime",
      "application.status", "application.name", "application.version"};
  private static final String[] DISK_METRICS = {"disk.free", "disk.total", "disk.usable"};

  private static final String[] JDBC_METRICS = {"jdbc.connections.active", "jdbc.connections.max", "jdbc.connections.min", "jdbc.connections.idle"};

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    log.debug("Creating TimedAspect for {}", registry.getClass().getSimpleName());
    return new TimedAspect(registry);
  }

}
