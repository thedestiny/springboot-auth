package com.platform.contract.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


@Slf4j
@Configuration
public class OkhttpConfig {

    private Long connectTimeout = 30L;

    private Long readTimeout = 30L;

    private Long writeTimeout = 30L;

    private Integer maxIdleConnections = 100;

    private Long keepAliveDuration = 30L;

    @Bean
    public OkHttpClient esignOkHttpClient() {

        return new OkHttpClient.Builder()
                .sslSocketFactory(this.sslSocketFactory(), this.x509TrustManager())
                .retryOnConnectionFailure(true)
                .connectionPool(this.connectionPool())
                .connectTimeout(this.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(this.readTimeout, TimeUnit.SECONDS)
                .writeTimeout(this.writeTimeout, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(this.maxIdleConnections, this.keepAliveDuration, TimeUnit.SECONDS);
    }

    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[]) null, new TrustManager[]{this.x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            log.error("sslSocketFactory error [{}]", e.getMessage(), e);
            return null;
        }
    }

}
