package kts.dev.ktsbk.client;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslContextOption;
import kts.dev.ktsbk.common.services.KtsBkServiceC2S;
import kts.dev.ktsbk.common.utils.AuthContext;
import kts.dev.ktsbk.common.utils.Pair;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationDefaults;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.security.KeyStore;

public class KtsBkApiProvider {
    public static final KtsBkApiProvider INSTANCE = new KtsBkApiProvider();
    // 79.137.202.47
    //KtsBkWsClient ws = new KtsBkWsClient(URI.create("ws://79.137.202.47"), new KtsBkServiceS2CImpl());
    KtsBkWsClient ws = new KtsBkWsClient(URI.create("ws://localhost:8001"), new KtsBkServiceS2CImpl());
    private KtsBkApiProvider() {
        KtsbkConfig.config.load();
        if(KtsbkConfig.config.getToken() != null) {
            ws.addHeader("token", KtsbkConfig.config.getToken());
        }
        /*
        try {
            //String currentPath = new java.io.File(".").getCanonicalPath();
            //System.out.println("Current dir:" + currentPath);

            String STORETYPE = "JKS";
            String KEYSTORE = Paths.get("config", "ktsbk.jks").toString();//"src", "main", "resources", "ktsbk-selfsigned.jks").toString();
            String STOREPASSWORD = "n9S4jOA9dCejrE3eqbJvHQ3dGL7EWkRs";
            String KEYPASSWORD = "z3wGkND7gqGFY3BC24GZRpwHcXnsqgwX";

            KeyStore ks = KeyStore.getInstance(STORETYPE);
            File kf = new File(KEYSTORE);
            ks.load(new FileInputStream(kf), null);//STOREPASSWORD.toCharArray());


            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, KEYPASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            //sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates

            SSLSocketFactory factory = sslContext
                    .getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

            ws.setSocketFactory(factory);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        */
    }
    protected String token;
    public void setToken(String token) {
        ws.addHeader("token", token);
        this.token = token;
        KtsbkConfig.config.setToken(token);
        KtsbkConfig.config.save();
    }

    public void setAddress(String address) {
        try {
            if (ws.isOpen()) ws.closeBlocking();
            ws = new KtsBkWsClient(URI.create("ws://" + address), new KtsBkServiceS2CImpl());
            ws.addHeader("token", KtsbkConfig.config.getToken());
        } catch (Exception ignore) {}
    }

    public AuthContext auth() {
        return null;
    }

    public KtsBkServiceC2S getCacheService() {
        return ws.service;
    }


}
