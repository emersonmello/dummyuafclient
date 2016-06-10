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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;

import org.ebayopensource.fidouaf.marvin.client.OperationalParamsIntf;
import org.ebayopensource.fidouaf.marvin.client.RegRecord;
import org.ebayopensource.fidouaf.marvin.client.StorageInterface;
import org.ebayopensource.fidouaf.marvin.client.crypto.SHA;
import org.ebayopensource.fidouaf.marvin.client.msg.Version;
import org.ebayopensource.fidouaf.marvin.client.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fidouaf.marvin.client.tlv.TagsEnum;
import org.ebayopensource.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

import br.edu.ifsc.mello.dummyuafclient.fidouaflib.AttachmentHintEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.Authenticator;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.KeyProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.MatcherProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.TCDEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.UserVerifyEnum;

/**
 * Created by npesic on 5/24/16.
 */
public class OperationalParams implements OperationalParamsIntf {
    public static String AAID = "EBA0#0001";
    public static byte[] defaultAttestCert = android.util.Base64.decode ("MIIB9zCCAZ+gAwIBAgIEV0ao6DAJBgcqhkjOPQQBMIGEMQswCQYDVQQGEwJVUzELMAkGA1UECAwCQ0ExETAPBgNVBAcMCFNhbiBKb3NlMRMwEQYDVQQKDAplQmF5LCBJbmMuMQwwCgYDVQQLDANUTlMxEjAQBgNVBAMMCWVCYXksIEluYzEeMBwGCSqGSIb3DQEJARYPbnBlc2ljQGViYXkuY29tMB4XDTE2MDUyNjA3NDIzM1oXDTE2MDYwNTA3NDIzM1owgYQxCzAJBgNVBAYTAlVTMQswCQYDVQQIDAJDQTERMA8GA1UEBwwIU2FuIEpvc2UxEzARBgNVBAoMCmVCYXksIEluYy4xDDAKBgNVBAsMA1ROUzESMBAGA1UEAwwJZUJheSwgSW5jMR4wHAYJKoZIhvcNAQkBFg9ucGVzaWNAZWJheS5jb20wWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAARt45XjwaE0nV8B8qqhQGlXG9A/HoyBYUvF2WUAPGdH/Jc+B/yFCyEOV+HwbOVh6LpgMOwHCAhe68oEkva7Mm22MAkGByqGSM49BAEDRwAwRAIgbsqkzOb9dA2t3Y2NNLiWzmIeCHRm/e5H+KJsT1D8z+MCIAjBSjxq5kRAo/WQuN0+FObzXlParCwxJd2xWP1l3rxM", android.util.Base64.DEFAULT);
    public static byte[] defaultAttestPrivKey = android.util.Base64.decode("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgy51Vu4OJrB9lceKjxyEtxqQkp4RfbN0WwfUlNdRMSjegCgYIKoZIzj0DAQehRANCAARt45XjwaE0nV8B8qqhQGlXG9A_HoyBYUvF2WUAPGdH_Jc-B_yFCyEOV-HwbOVh6LpgMOwHCAhe68oEkva7Mm22", android.util.Base64.URL_SAFE);
    public static byte[] defaultAttestPubKey = android.util.Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEbeOV48GhNJ1fAfKqoUBpVxvQPx6MgWFLxdllADxnR_yXPgf8hQshDlfh8GzlYei6YDDsBwgIXuvKBJL2uzJttg", android.util.Base64.URL_SAFE);
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String aaid;
    private byte[] attestCert;
    private byte[] attestPrivKey;
    private StorageInterface storage;
    private String appIdPrefix = "appId::";
    private Authenticator authenticator;

    @Override
    public Authenticator getAuthenticator(){
        return this.authenticator;
    }

    @Override
    public String getAAID() {
        return aaid;
    }

    @Override
    public byte[] getAttestCert() {
        return attestCert;
    }

    @Override
    public long getRegCounter() {
        return Preferences.getSettingsParamLong("regCounter");
    }

    @Override
    public void incrementRegCounter() {
        long regCount = Preferences.getSettingsParamLong("regCounter");
        Preferences.setSettingsParamLong("regCounter", regCount);
    }

    @Override
    public long getAuthCounter() {
        return 0;
    }

    @Override
    public void incrementAuthCounter() {

    }

    @Override
    public boolean isFacetIdValid(String s, String s1) {
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public byte[] signWithAttestationKey(byte[] signedDataValue) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("EC");
        byte[] signature = null;
        try {
            PrivateKey privateKey =
                    kf.generatePrivate(new PKCS8EncodedKeySpec(attestPrivKey));
            java.security.Signature s = java.security.Signature.getInstance("SHA256withECDSA");
            s.initSign(privateKey);
            s.update(SHA.sha(signedDataValue, "SHA-256"));
            signature = s.sign();
        }catch(KeyPermanentlyInvalidatedException invalidatedKeyException) {
            logger.info("invalidatedKeyException="+invalidatedKeyException);
            //Can happen when user removes the screen lock
            throw new Exception ("KeyInvalidatedByAndroidKeyStore");
        }catch(Exception e){
            logger.info("e="+e);
            throw new Exception ("SystemError");
        }
        return signature;
    }

    @Override
    public StorageInterface getStorage() {
        return storage;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public KeyPairGenerator getKeyPairGenerator(String keyId) {
        try {
//            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//            KeyStore.Entry entry = keyStore.getEntry(keyId, null);
//            if (entry != null) {
//                //TODO Should I return an error to Android RP app? Hey! You have to dereg first.
//                Log.i("createKeyPair", "There is already a keypair for this RP endpoint. Ok it will be overwritten.");
//            }
//
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
//            ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
//            keyPairGenerator.initialize(
//                    new KeyGenParameterSpec.Builder(keyName,
//                            KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
//                            .setDigests(KeyProperties.DIGEST_SHA256,
//                                    KeyProperties.DIGEST_SHA384,
//                                    KeyProperties.DIGEST_SHA512)
//                            .setAlgorithmParameterSpec(ecGenSpec)
//                            .setUserAuthenticationRequired(true)
//                            .setUserAuthenticationValidityDurationSeconds(60)
//                            .build());


            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            keyId,
                            KeyProperties.PURPOSE_SIGN)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            .setDigests(KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA384,
                                    KeyProperties.DIGEST_SHA512)
                            // Only permit the private key to be used if the user authenticated
                            // within the last five minutes.
                            .setUserAuthenticationRequired(true)
                            .setUserAuthenticationValidityDurationSeconds(5 * 60)
                            .build());
            return keyPairGenerator;
        }catch (Exception e){
            logger.info("getKeyPairGenerator: e="+e);
            return null;
        }
    }

    @Override
    public RegRecord genAndRecord(String appId) {

        String keyId = genKeyId();
        Preferences.setSettingsParam(appIdPrefix+appId, keyId);
        RegRecord record = new RegRecord(
                keyId,
                getKeyPairGenerator(keyId).generateKeyPair().getPublic().getEncoded()
        );
        storage.addRecord(record);
        return record;
    }

    private String genKeyId() {
        String tmp = getFacetId() + System.currentTimeMillis();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(tmp.getBytes());
            return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFacetId(String appId) {
        /**
         * Skipping the check if the facet Id belong to a list of app's trusted.
         * This will save one service call.
         * It leaves the check to be done on the server.
         */
        return getFacetId();
    }

    public String getFacetId (){
        StringBuffer ret = new StringBuffer();
        String comma = "";
        try {
            Context context = ApplicationContextProvider.getContext();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(Preferences.getSettingsParam("callingPackageName"), PackageManager.GET_SIGNATURES);
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature sign: packageInfo.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                messageDigest.update(sign.toByteArray());
                String currentSignature = Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
                ret.append("android:apk-key-hash:");
                ret.append(currentSignature.substring(0, currentSignature.length() - 2));
                ret.append(comma);
                comma = ",";
            }
            return ret.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getKeyId(String appId) {
        return Preferences.getSettingsParam(appIdPrefix+appId).trim()+"\n";
    }

    @Override
    public void init(String aaid, byte[] attestCert, byte[] attestPrivKey, StorageInterface storage) {
        this.aaid = aaid;
        this.attestCert = attestCert;
        this.attestPrivKey = attestPrivKey;
        this.storage = storage;
        this.authenticator = new Authenticator();
        this.fillAuthenticatorDetails();
        this.authenticator.aaid = aaid;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public byte[] getSignature(byte[] signedDataValue, String keyId) throws Exception {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        PrivateKey privateKey = (PrivateKey) ks.getKey(keyId, null);
        byte[] signature = null;
        try {
            java.security.Signature s = java.security.Signature.getInstance("SHA256withECDSA");
            s.initSign(privateKey);
            s.update(SHA.sha(signedDataValue, "SHA-256"));
            signature = s.sign();
        }catch(KeyPermanentlyInvalidatedException invalidatedKeyException) {
            //Can happen when user removes the screen lock
            throw new Exception ("KeyInvalidatedByAndroidKeyStore");
        }catch(Exception e){
            throw new Exception ("SystemError");
        }
        return signature;
    }

    public boolean removeKey(String appId){
        String keyId = this.getKeyId(appId);
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            ks.deleteEntry(keyId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void fillAuthenticatorDetails() {
        Version version = new Version(1, 0);

        authenticator.title = "Dummy UAF Authenticator";
        authenticator.aaid = "DMY0#0001";
        authenticator.description = "A dummy UAF Client suitable to conduct development tests on smartphones that are not FIDO Ready.";
        authenticator.supportedUAFVersions = new Version[1];
        authenticator.supportedUAFVersions[0] = version;
        authenticator.assertionScheme = "UAFV1TLV";
        authenticator.authenticationAlgorithm = (short) AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW.id;
        authenticator.attestationTypes = new short[1];
        authenticator.attestationTypes[0] = (short) TagsEnum.TAG_ATTESTATION_BASIC_FULL.id;
        authenticator.userVerification = UserVerifyEnum.USER_VERIFY_FINGERPRINT.getValue();
        authenticator.keyProtection = (short) (KeyProtectionEnum.KEY_PROTECTION_HARDWARE.getValue() | KeyProtectionEnum.KEY_PROTECTION_TEE.getValue());
        authenticator.matcherProtection = MatcherProtectionEnum.MATCHER_PROTECTION_TEE.getValue();
        authenticator.attachmentHint = AttachmentHintEnum.ATTACHMENT_HINT_INTERNAL.getValue();
        authenticator.isSecondFactorOnly = false;
        authenticator.tcDisplay = TCDEnum.TRANSACTION_CONFIRMATION_DISPLAY_ANY.getValue();
        authenticator.tcDisplayContentType = "text/plain";
        authenticator.tcDisplayPNGCharacteristics = null;
        authenticator.icon = "https://github.com/emersonmello/dummyuafclient/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher.png";
        authenticator.supportedExtensionIDs = null;
    }

}
