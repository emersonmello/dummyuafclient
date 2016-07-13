/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.ebayopensource.fidouaf.marvin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.jce.X509Principal;
import org.spongycastle.x509.X509V3CertificateGenerator;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

public class ApplicationContextProvider extends Application {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static final String ATTEST_KEY = "UAFAttestKey";

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        generateAttestationKey(false);
    }

    @SuppressWarnings("deprecation")
    public static byte[] generateAttestationKey(boolean overwriteCurrentKey) {
        byte[] certDecoded = new byte[0];
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            PrivateKey privateKey = (PrivateKey) ks.getKey(ATTEST_KEY, null);
            if (overwriteCurrentKey) {
                privateKey = null;
            }
            if (privateKey == null) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
                ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(ATTEST_KEY,
                                KeyProperties.PURPOSE_SIGN |
                                        KeyProperties.PURPOSE_ENCRYPT |
                                        KeyProperties.PURPOSE_DECRYPT |
                                        KeyProperties.PURPOSE_VERIFY)
                                .setDigests(KeyProperties.DIGEST_SHA1,
                                        KeyProperties.DIGEST_SHA256,
                                        KeyProperties.DIGEST_SHA384,
                                        KeyProperties.DIGEST_SHA512)
                                .setAlgorithmParameterSpec(ecGenSpec)
                                .build());

                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
                gen.setPublicKey(keyPair.getPublic());
                gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
                Hashtable<ASN1ObjectIdentifier, String> attrs = new Hashtable<ASN1ObjectIdentifier, String>();
                Vector<ASN1ObjectIdentifier> vOrder = new Vector<ASN1ObjectIdentifier>();
                attrs.put(X509Principal.CN, "Dummy FIDO Client Inc.");
                vOrder.add(0, X509Principal.CN);
                attrs.put(X509Principal.O, "Dummy");
                vOrder.add(0, X509Principal.O);
                attrs.put(X509Principal.C, "BR");
                vOrder.add(0, X509Principal.C);
                gen.setIssuerDN(new X509Principal(vOrder, attrs));
                gen.setSubjectDN(new X509Principal(vOrder, attrs));

                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 10);
                gen.setNotBefore(start.getTime());
                gen.setNotAfter(end.getTime());

                gen.setSignatureAlgorithm("SHA1WithECDSA");

                // Workaround - see https://android.googlesource.com/platform/frameworks/base/+/marshmallow-mr1-release/keystore/java/android/security/keystore/AndroidKeyStoreBCWorkaroundProvider.java
//            <p>This provider was separated out of {@link AndroidKeyStoreProvider} to work around the issue
//                    * that Bouncy Castle provider incorrectly declares that it accepts arbitrary keys (incl. Android
//                    * KeyStore ones). This causes JCA to select the Bouncy Castle's implementation of JCA crypto
//                    * operations for Android KeyStore keys unless Android KeyStore's own implementations are installed
//                    * as higher-priority than Bouncy Castle ones. The purpose of this provider is to do just that: to
//                    * offer crypto operations operating on Android KeyStore keys and to be installed at higher priority
//            * than the Bouncy Castle provider.
                X509Certificate cert = gen.generate(keyPair.getPrivate(), "AndroidKeyStoreBCWorkaround");
                certDecoded = cert.getEncoded();



                byte[] coded = Base64.encode(certDecoded, Base64.DEFAULT);
                String stringCertCoded = new String(coded);

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(sContext);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(ATTEST_KEY + "_CERT", stringCertCoded);
                editor.apply();
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(sContext);
                String certEncoded = settings.getString(ATTEST_KEY + "_CERT", "");
                certDecoded = android.util.Base64.decode(certEncoded.getBytes(), Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.d("X.509", "Error to generate attestation key: " + e.toString());
        }
        return certDecoded;
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
}
