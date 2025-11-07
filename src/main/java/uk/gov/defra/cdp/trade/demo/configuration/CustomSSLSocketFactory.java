package uk.gov.defra.cdp.trade.demo.configuration;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Custom SSLSocketFactory for PostgreSQL connections that uses the application's
 * custom SSLContext (which includes RDS and CDP certificates).
 *
 * PostgreSQL JDBC driver requires a SSLSocketFactory class that can be instantiated
 * via reflection. This factory delegates to the custom SSLContext configured in
 * TrustStoreConfiguration.
 */
@Slf4j
public class CustomSSLSocketFactory extends SSLSocketFactory {

    private static SSLContext customSslContext;
    private final SSLSocketFactory delegate;

    /**
     * Sets the custom SSLContext to be used by all instances.
     * This must be called before PostgreSQL attempts to create connections.
     */
    public static void setCustomSslContext(SSLContext sslContext) {
        customSslContext = sslContext;
        log.debug("Custom SSLContext set for PostgreSQL connections");
    }

    /**
     * Default constructor required by PostgreSQL JDBC driver.
     * Uses the custom SSLContext if available, otherwise falls back to default.
     */
    public CustomSSLSocketFactory() {
        if (customSslContext != null) {
            this.delegate = customSslContext.getSocketFactory();
            log.debug("Using custom SSLContext for PostgreSQL connection");
        } else {
            try {
                this.delegate = SSLContext.getDefault().getSocketFactory();
                log.warn("Custom SSLContext not set, using default SSLContext");
            } catch (Exception e) {
                throw new IllegalStateException("Failed to initialize SSLSocketFactory", e);
            }
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {
        return delegate.createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return delegate.createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        return delegate.createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return delegate.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return delegate.createSocket(address, port, localAddress, localPort);
    }
}
