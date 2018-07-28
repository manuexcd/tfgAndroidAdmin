package spring.es.admintfg;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MyAsyncHttpClient {
    private static AsyncHttpClient client;

    public MyAsyncHttpClient() {
        super();
    }

    public static AsyncHttpClient getAsyncHttpClient(Context context) {
        try {
            CertificateFactory certificateFactory;
            certificateFactory = CertificateFactory.getInstance("X.509");

            Certificate ca;
            try {
                ca = certificateFactory.generateCertificate(context.getResources().openRawResource(R.raw.cert));
            } finally {
                try {
                    context.getResources().openRawResource(R.raw.cert).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Put the certificates a key store.
            char[] certPass = "password".toCharArray(); // Any password will work.
            KeyStore keyStore;
            keyStore = KeyStore.getInstance("BKS", "BC");
            keyStore.load(context.getResources().openRawResource(R.raw.keystore), certPass);
            Objects.requireNonNull(keyStore).setCertificateEntry("ca", ca);

            // Use it to build an X509 trust manager.
            TrustManagerFactory trustManagerFactory;
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            MySSLSocketFactory socketFactory;
            socketFactory = new MySSLSocketFactory(keyStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            client = new AsyncHttpClient();
            client.setSSLSocketFactory(socketFactory);
        } catch (CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableKeyException | NoSuchProviderException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        return client;
    }
}
