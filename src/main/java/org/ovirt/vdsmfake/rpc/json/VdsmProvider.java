package org.ovirt.vdsmfake.rpc.json;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.ovirt.vdsm.jsonrpc.client.reactors.ManagerProvider;
import org.slf4j.Logger;


class VdsmProvider extends ManagerProvider {

    public static final String TYPE = "vdsm";
    private static final String CONFIG_PATH = "/usr/lib/python2.7/site-packages/vdsm/config.py";
    private String path;
    private Logger logger;

    VdsmProvider(String path, Logger logger) {
        this.logger = logger;
        this.logger.debug("Using VdsmProvider, certs path: {}", path);
        if (path.length() > 0 ){
            this.path = path;
        }else {
            Pattern pattern = Pattern.compile("'trust_store_path', '(.*)'");
            try (BufferedReader reader = new BufferedReader(new FileReader(path == null ? CONFIG_PATH : path))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        this.path = matcher.group(1);
                        break;
                    }
                }
            } catch (IOException ignored) {
                logger.error("failed to set up certs for {} - {}", this.path, ignored);
                // checked path when loading a Manager
            }
        }
    }

    @Override
    public TrustManager[] getTrustManagers() throws GeneralSecurityException {
        validate(this.path);
        try {
            Path certPath = Paths.get(this.path + File.separator + "certs" + File.separator + "cacert.pem");
            this.logger.debug("certpath: {}", certPath.toString());
            byte[] certData = Files.readAllBytes(certPath);

            X509Certificate cert = (X509Certificate) generateCertificateFromPEM(certData);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", cert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);
            return tmf.getTrustManagers();
        } catch (IOException e) {
            throw new GeneralSecurityException(e);
        }
    }

    public static void validate(String path) throws GeneralSecurityException {
        if (path == null || "".equals(path.trim())) {
            throw new GeneralSecurityException("Configuration file not found");
        }
    }

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException {
        validate(this.path);
        try {
            Path keyPath = Paths.get(this.path + File.separator + "keys" + File.separator + "vdsmkey.pem");
            this.logger.debug("vdsmkey: {}", keyPath.toString());
            byte[] keyData = Files.readAllBytes(keyPath);

            Path certPath = Paths.get(this.path + File.separator + "certs" + File.separator + "vdsmcert.pem");
            this.logger.debug("vdsmcert: {}", certPath.toString());
            byte[] certData = Files.readAllBytes(certPath);

            X509Certificate cert = (X509Certificate) generateCertificateFromPEM(certData);
            PrivateKey key = (PrivateKey) generatePrivateKeyFromPEM(keyData);

            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(null);
            keystore.setCertificateEntry("cert-alias", cert);
            keystore.setKeyEntry("key-alias", key, "changeit".toCharArray(), new Certificate[] { cert });

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, "changeit".toCharArray());
            return kmf.getKeyManagers();
        } catch (IOException e) {
            throw new GeneralSecurityException(e);
        }
    }

    protected static PrivateKey generatePrivateKeyFromPEM(byte[] keyBytes)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        try (ByteArrayInputStream pemByteIn = new ByteArrayInputStream(keyBytes);
             PEMReader reader = new PEMReader(new InputStreamReader(pemByteIn))) {
            KeyPair keyPair = (KeyPair) reader.readObject();
            return keyPair.getPrivate();
        }
    }

    protected static X509Certificate generateCertificateFromPEM(byte[] certBytes) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(
                new ByteArrayInputStream(certBytes));
    }
}