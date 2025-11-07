# AWS RDS IAM Authentication Setup

This document describes the AWS RDS IAM authentication implementation for connecting to Aurora Serverless Postgres.

## Overview

The application now supports two connection modes:
1. **Local Development**: Standard username/password authentication to docker-compose PostgreSQL
2. **AWS/CDP Environment**: IAM authentication with short-lived tokens to AWS RDS Aurora Serverless

## Implementation Details

### Files Created

1. **RdsIamDataSourceConfiguration.java** (`src/main/java/uk/gov/defra/cdp/trade/demo/configuration/`)
   - Conditionally creates a DataSource with AWS RDS IAM authentication
   - Only activates when `aws.rds.iam.enabled=true`
   - Generates short-lived tokens (15 min validity) from AWS RDS
   - Auto-refreshes tokens every 10 minutes via HikariCP max-lifetime
   - Uses custom SSLSocketFactory for TLS with custom certificates

2. **CustomSSLSocketFactory.java** (`src/main/java/uk/gov/defra/cdp/trade/demo/configuration/`)
   - Custom SSLSocketFactory for PostgreSQL JDBC driver
   - Uses the application's custom SSLContext (includes RDS + CDP certificates)
   - Required because PostgreSQL needs a factory that can be instantiated via reflection

### Files Modified

1. **pom.xml**
   - Added AWS SDK dependencies:
     - `software.amazon.awssdk:rds` (2.32.0)
     - `software.amazon.awssdk:auth` (2.32.0)

2. **application.yml**
   - Added `aws.rds.certificate` property for RDS CA certificate
   - Added `aws.rds.iam.enabled` flag (defaults to `false`)

3. **CertificateLoader.java**
   - Added `loadRdsCertificate()` method
   - Reads from `TRUSTSTORE_RDS_ROOT_CA` environment variable

4. **TrustStoreConfiguration.java**
   - Updated to include both CDP and RDS certificates in SSLContext
   - Handles case where neither certificate is present (uses JVM defaults)

5. **Test Files**
   - Updated `CertificateLoaderTest.java` with 5 new RDS certificate tests
   - Updated `TrustStoreConfigurationIT.java` with 1 new RDS certificate test
   - All tests pass (55 unit + 24 integration tests)

## Configuration

### Local Development (Default)

No configuration needed. The application uses the datasource configured in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_WRITE_HOSTNAME:postgres}:5432/trade-commodity-codes
    username: trade_commodity_codes
    password: <from application-dev.yml>
```

### AWS/CDP Environment

Set the following environment variables in cdp-app-config:

#### Required
- `AWS_RDS_IAM_ENABLED=true` - Enables RDS IAM authentication
- `POSTGRES_WRITE_HOSTNAME` - RDS instance hostname (e.g., `mydb.cluster-xxx.eu-west-2.rds.amazonaws.com`)
- `spring.datasource.username` - Database username (should match IAM role)
- `spring.datasource.database` - Database name

#### Optional
- `TRUSTSTORE_RDS_ROOT_CA` - Base64-encoded RDS CA certificate (PEM format)
  - Download from: https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem
  - Encode: `base64 -i global-bundle.pem | tr -d '\n'`
- `spring.datasource.port` - Database port (defaults to 5432)
- `POSTGRES_MAX_POOL_SIZE` - HikariCP max pool size (defaults to 10)
- `POSTGRES_MAX_LIFETIME` - Connection max lifetime in ms (defaults to 600000 = 10 min)

## How It Works

### Token Generation
1. When `aws.rds.iam.enabled=true`, `RdsIamDataSourceConfiguration` creates the datasource
2. Uses AWS SDK `RdsUtilities` to generate authentication token
3. Token is generated using IAM credentials from the environment (no password needed)
4. Token is valid for 15 minutes

### Token Refresh
1. HikariCP `max-lifetime` set to 10 minutes (less than token expiry)
2. Forces connections to be recreated before tokens expire
3. New token generated automatically on reconnection

### SSL/TLS
1. `CustomSSLSocketFactory` uses the application's custom SSLContext
2. SSLContext includes:
   - Default JVM trust store certificates
   - CDP root CA (from `TRUSTSTORE_CDP_ROOT_CA`)
   - RDS root CA (from `TRUSTSTORE_RDS_ROOT_CA`)
3. PostgreSQL JDBC connects with `ssl=true&sslmode=require`

## Testing

All tests pass successfully:
- **Unit Tests**: 55 tests (including 9 certificate tests)
- **Integration Tests**: 24 tests (application starts successfully)

Run tests:
```bash
mvn clean test          # Unit tests
mvn integration-test    # Integration tests
```

## IAM Setup Requirements

### RDS Database Configuration
1. Enable IAM authentication on the RDS cluster
2. Create database user mapped to IAM role:
   ```sql
   CREATE USER trade_commodity_codes;
   GRANT rds_iam TO trade_commodity_codes;
   GRANT ALL PRIVILEGES ON DATABASE trade_commodity_codes TO trade_commodity_codes;
   ```

### IAM Policy
The application's IAM role needs:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "rds-db:connect",
      "Resource": "arn:aws:rds-db:eu-west-2:<account-id>:dbuser:<db-resource-id>/trade_commodity_codes"
    }
  ]
}
```

## Troubleshooting

### Local Development Issues
- Ensure `aws.rds.iam.enabled` is NOT set (defaults to false)
- Check docker-compose postgres is running: `docker-compose ps`
- Verify application-dev.yml has postgres password

### AWS Connection Issues
1. **Token Generation Fails**
   - Check IAM role has `rds-db:connect` permission
   - Verify AWS credentials are available in environment
   - Check CloudWatch logs for detailed error messages

2. **SSL/TLS Errors**
   - Ensure `TRUSTSTORE_RDS_ROOT_CA` is set with correct certificate
   - Certificate must be base64-encoded PEM format
   - Check certificate is not expired

3. **Database User Not Found**
   - Verify database user created with `GRANT rds_iam`
   - Ensure username matches `spring.datasource.username`
   - Check IAM policy has correct database resource ARN

## References

- [AWS RDS IAM Database Authentication](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.html)
- [RDS Certificate Bundle](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.SSL.html)
- [PostgreSQL JDBC SSL Documentation](https://jdbc.postgresql.org/documentation/ssl/)
