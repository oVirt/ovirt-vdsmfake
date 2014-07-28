package org.ovirt.vdsmfake.rpc.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.ovirt.vdsm.jsonrpc.client.reactors.ManagerProvider;

public class FakeVDSMSSLProvider extends ManagerProvider {

    private static final String TRUST_STORE_FORMAT = "JKS";
    private static final String KEY_STORE_FORMAT = "PKCS12";
    private String keyStorePassword;
    private String trustStorePassword;
    private String keyStoreLocation;
    private String trustStoreLocation;
    private KeyManager[] keyManagers;
    private TrustManager[] trustManagers;

    public FakeVDSMSSLProvider(String keyStore, String trustStore, String keyStorePassword, String trustStorePassword) {
        this.keyStoreLocation = keyStore;
        this.trustStoreLocation = trustStore;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;

    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException {
        if (trustManagers == null) {
            TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmfactory.init(getTrustStore());
            trustManagers = tmfactory.getTrustManagers();
        }

        return trustManagers;
    }

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException {
        if (keyManagers == null) {
            KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmfactory.init(getKeyStore(), keyStorePassword.toCharArray());
            keyManagers = kmfactory.getKeyManagers();
        }

        return keyManagers;
    }

    /**
     * Return the engine keystore.
     * @return engine key store.
     */
    public KeyStore getKeyStore() {
        File keystoreFile = new File(keyStoreLocation);

        if (keyStorePassword == null) {
            return getKeyStore(KEY_STORE_FORMAT, keystoreFile, null);
        } else {
            return getKeyStore(KEY_STORE_FORMAT, keystoreFile, keyStorePassword.toCharArray());
        }
    }

    /**
     * Return the trust keystore.
     * @return engine key store.
     */
    public KeyStore getTrustStore() {
        File truststoreFile = new File(trustStoreLocation);

        if (trustStorePassword == null) {
            return getKeyStore(TRUST_STORE_FORMAT, truststoreFile, null);
        } else {
            return getKeyStore(TRUST_STORE_FORMAT, truststoreFile, trustStorePassword.toCharArray());
        }
    }

    private static KeyStore getKeyStore(String type, File file, char[] password) {
        try (final InputStream in = new FileInputStream(file)) {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(in, password);
            return ks;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "Failed to load local keystore '%1$s'",
                            file
                            ),
                    e);
        }
    }

}
