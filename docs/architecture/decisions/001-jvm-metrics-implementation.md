# ADR 001: JVM Metrics Implementation

**Status:** Accepted
**Date:** 2026-01-07
**Deciders:** Development Team

## Context

The trade-commodity-codes service requires metrics collection for operational monitoring in AWS CloudWatch. We needed to decide:

1. How to collect metrics (instrumentation approach)
2. What metrics to collect (JVM, HTTP, DB, custom)
3. How to publish to CloudWatch (push vs pull, format)
4. How to prevent unbounded metric growth

## Decision

We will implement metrics using:

1. **Collection: Micrometer + Spring Boot Actuator**
   - Standard JVM, HTTP, and database metrics via auto-configuration
   - Controller timing via Spring AOP and `@Timed` annotations
   - Configurable metric enabling/disabling via YAML properties

2. **Publishing: AWS Embedded Metrics Format (EMF)**
   - Scheduled batch publishing every 60 seconds
   - Custom `EmfMetricsPublisher` service pushes to CloudWatch Logs
   - Metrics automatically appear in CloudWatch Metrics via EMF parsing

3. **Cardinality Control: Automatic Cleanup**
   - Controller metrics (high-cardinality) removed after each publish cycle
   - JVM/DB metrics (low-cardinality) persist for continuous monitoring
   - Prefix-based filtering (`controller.*`) for selective cleanup

4. **Configuration: Environment-based**
   - Metrics disabled by default in production
   - Enable via `METRICS_ENABLED=true` environment variable
   - Fine-grained control via `management.metrics.enable.*` properties

## Consequences

### Positive

- **Zero code changes for standard metrics**: Spring Boot Actuator provides JVM/HTTP/DB metrics automatically
- **Low overhead**: AOP-based `@Timed` annotations add minimal performance impact (~1-2%)
- **CloudWatch native**: EMF format integrates seamlessly with AWS monitoring
- **Unbounded growth prevented**: Automatic cleanup of high-cardinality controller metrics
- **Cost control**: Metrics disabled by default, enabled only when needed

### Negative

- **60-second publishing delay**: Metrics not real-time (acceptable for monitoring use case)
- **Cleanup complexity**: Controller metrics are ephemeral, requiring recreation per request
- **EMF dependency**: Tied to AWS CloudWatch, not portable to other observability platforms

### Neutral

- **Test isolation**: Metrics work in tests but can be disabled via test profile if needed
- **LocalStack compatibility**: EMF works with LocalStack for local development testing

## Alternatives Considered

### Alternative 1: Micrometer CloudWatch Registry (pull-based)

**Rejected because:**
- Requires CloudWatch API calls from application (network overhead)
- More complex IAM permissions (PutMetricData)
- Higher AWS costs (direct API calls vs log ingestion)

### Alternative 2: Prometheus + CloudWatch Exporter

**Rejected because:**
- Additional infrastructure component (Prometheus server)
- Pull-based model less suitable for ECS Fargate environment
- More complex operational overhead

### Alternative 3: Manual Instrumentation (no AOP)

**Rejected because:**
- Requires code changes in every controller method
- Easy to forget instrumentation for new endpoints
- Harder to maintain consistent naming conventions

## Implementation Notes

**Key Files:**
- `MetricsConfig.java`: Registers `TimedAspect` and `CountedAspect` beans
- `EmfMetricsPublisher.java`: Scheduled metrics publishing service
- `application.yml`: Metric enable/disable configuration

**Testing:**
- Unit tests verify bean creation and metrics publishing logic
- Integration tests validate `@Timed` annotations record metrics correctly
- Manual testing confirms CloudWatch delivery via LocalStack

**Documentation:**
- README.md contains comprehensive metrics usage guide
- Inline Javadoc explains architecture decisions
- This ADR provides high-level context

## References

- [Micrometer Documentation](https://micrometer.io/docs)
- [AWS EMF Specification](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch_Embedded_Metric_Format.html)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)
