package br.edu.ifsc.mello.dummyuafclient.fidoauthenticator;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import org.ebayopensource.fido.uaf.client.AttestCert;
import org.ebayopensource.fido.uaf.crypto.Asn1;
import org.ebayopensource.fido.uaf.crypto.KeyCodec;
import org.ebayopensource.fido.uaf.crypto.NamedCurve;
import org.ebayopensource.fido.uaf.crypto.SHA;
import org.ebayopensource.fido.uaf.msg.Version;
import org.ebayopensource.fido.uaf.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fido.uaf.tlv.TagsEnum;
import org.ebayopensource.fidouafclient.util.ApplicationContextProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import br.edu.ifsc.mello.dummyuafclient.fidouafclient.AttachmentHintEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouafclient.Authenticator;
import br.edu.ifsc.mello.dummyuafclient.fidouafclient.KeyProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouafclient.MatcherProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouafclient.TCDEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouafclient.UserVerifyEnum;

/**
 * A FIDO UAF Authenticator is a secure entity, connected to or housed within FIDO user devices, that
 * can create key material associated to a Relying Party. The key can then be used to participate in
 * FIDO UAF strong authentication protocols. For example, the FIDO UAF Authenticator can provide a
 * response to a cryptographic challenge using the key material thus authenticating itself to the Relying Party.
 * <p>
 * In order to meet the goal of simplifying integration of trusted authentication capabilities,
 * a FIDO UAF Authenticator will be able to attest to its particular type (e.g., biometric) and
 * capabilities (e.g., supported crypto algorithms), as well as to its provenance. This provides a
 * Relying Party with a high degree of confidence that the user being authenticated is indeed the
 * user that originally registered with the site.
 * <p>
 * UAF authenticators may be connected to a user device via various physical interfaces (SPI, USB,
 * Bluetooth, etc). The UAF Authenticator-Specific module (ASM) is a software interface on top of
 * UAF authenticators which gives a standardized way for FIDO UAF Clients to detect and access the
 * functionality of UAF authenticators, and hides internal communication complexity from clients.
 * <p>
 * The ASM is a platform-specific software component offering an API to FIDO UAF Clients, enabling
 * them to discover and communicate with one or more available authenticators.
 * <p>
 * A single ASM may report on behalf of multiple authenticators.
 *
 * @link https://fidoalliance.org/specs/fido-uaf-v1.0-ps-20141208/fido-uaf-asm-api-v1.0-ps-20141208.html
 */
public class FidoUafAuthenticator {

    private static FidoUafAuthenticator instance;

    public synchronized static FidoUafAuthenticator getInstance() {
        if (instance == null) {
            instance = new FidoUafAuthenticator();
        }
        return instance;
    }

    private Authenticator authenticatorDetails = new Authenticator();
    private KeyStore mKeyStore;
    private KeyguardManager mKeyguardManager;
    private KeyPair mKeyPair;
    private static final String KEY_NAME = "attestKey";
    private static final int certificateIsValidFor = 100;
    private SharedPreferences mSharedPreferences;
    private long deviceCounter;


    public FidoUafAuthenticator() {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

        this.fillDetails();

        Context context = ApplicationContextProvider.getContext();

        this.mKeyguardManager = context.getSystemService(KeyguardManager.class);
        try {

            this.mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            this.mKeyStore.load(null);

            // Attestation Key + Certificate
            if (!((mKeyStore.getEntry(KEY_NAME, null)) instanceof KeyStore.PrivateKeyEntry)) {
                this.generateAttestationKey();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Authenticator getAuthenticatorDetails() {
        return authenticatorDetails;
    }


    public void generateAttestationKey() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, CertificateException {

        Calendar beginsOn = Calendar.getInstance();
        Calendar expiresOn = beginsOn;
        expiresOn.add(Calendar.YEAR, certificateIsValidFor);

        mKeyStore.load(null);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY | KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA384,
                                KeyProperties.DIGEST_SHA512)
                        .setCertificateNotBefore(beginsOn.getTime())
                        .setCertificateNotAfter(expiresOn.getTime())
                        .setCertificateSerialNumber(BigInteger.valueOf(System.currentTimeMillis()))
                        .setCertificateSubject(new X500Principal("CN=" + this.authenticatorDetails.title))
                        .build());
        this.mKeyPair = keyPairGenerator.generateKeyPair();
    }

    public KeyPair createKeyPair(String keyName) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, CertificateException, UnrecoverableEntryException, KeyStoreException {
        mKeyStore.load(null);

        KeyStore.Entry entry = mKeyStore.getEntry(keyName, null);
        if (entry != null) {
            //TODO Should I return an error to Android RP app? Hey! You have to dereg first.
            Log.i("createKeyPair", "There is already a keypair for this RP endpoint. Ok it will be overwritten.");
        }

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(keyName,
                        KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA384,
                                KeyProperties.DIGEST_SHA512)
                        .setAlgorithmParameterSpec(ecGenSpec)
                        .setUserAuthenticationRequired(true)
                        .setUserAuthenticationValidityDurationSeconds(60)
                        .build());
        return keyPairGenerator.generateKeyPair();
    }

//    public byte[] attestationSignature(byte[] dataForSigning) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, CertificateException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, SignatureException {
//        mKeyStore.load(null);
//        KeyStore.Entry entry = mKeyStore.getEntry(KEY_NAME, null);
//        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
//            return null;
//        }
//        Signature s = Signature.getInstance("SHA256withECDSA");
//        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
//        s.update(dataForSigning);
//        byte[] signature = s.sign();
//        return signature;
//
////        PrivateKey priv = (PrivateKey) mKeyStore.getKey(KEY_NAME, null);
////        BigInteger[] signatureGen = NamedCurve.signAndFromatToRS(KeyCodec.getPrivKey(priv.getEncoded()), SHA.sha(dataForSigning, "SHA-256"));
////        return signatureGen;
//    }
//
//    public boolean verifyAttestationSignature(byte[] dataForSigning, byte[] signatureGen) throws Exception {
//        mKeyStore.load(null);
//        KeyStore.Entry entry = mKeyStore.getEntry(KEY_NAME, null);
//        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
//            return false;
//        }
//        Signature s = Signature.getInstance("SHA256withECDSA");
//        s.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
//        s.update(dataForSigning);
//        boolean valid = s.verify(signatureGen);
//        return  valid;
//
////        PublicKey pub = (PublicKey) mKeyStore.getKey(KEY_NAME, null);
////        return NamedCurve.verify(
////                KeyCodec.getKeyAsRawBytes((java.security.interfaces.ECPublicKey)KeyCodec.getPubKey(pub.getEncoded())),
////        SHA.sha(dataForSigning, "SHA-256"),
////                Asn1.decodeToBigIntegerArray(Asn1.getEncoded(signatureGen)));
//    }

    public byte[] getRPSignature(String keyAlias, byte[] dataForSigning) {
        try {
            mKeyStore.load(null);
            KeyStore.Entry entry = mKeyStore.getEntry(keyAlias, null);
            byte[] signature = null;
            java.security.Signature s = java.security.Signature.getInstance("SHA256withECDSA");
            s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            s.update(SHA.sha(dataForSigning, "SHA-256"));
            signature = s.sign();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void fillDetails() {
        Version version = new Version(1, 0);

        authenticatorDetails.title = "Dummy UAF Authenticator";
        authenticatorDetails.aaid = "DMY0#0001";
        authenticatorDetails.description = "A dummy UAF Client suitable to conduct development tests on smartphones that are not FIDO Ready.";
        authenticatorDetails.supportedUAFVersions = new Version[1];
        authenticatorDetails.supportedUAFVersions[0] = version;
        authenticatorDetails.assertionScheme = "UAFV1TLV";
        authenticatorDetails.authenticationAlgorithm = (short) AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW.id;
        authenticatorDetails.attestationTypes = new short[1];
        authenticatorDetails.attestationTypes[0] = (short) TagsEnum.TAG_ATTESTATION_BASIC_FULL.id;
        authenticatorDetails.userVerification = UserVerifyEnum.USER_VERIFY_FINGERPRINT.getValue();
        authenticatorDetails.keyProtection = (short) (KeyProtectionEnum.KEY_PROTECTION_HARDWARE.getValue() | KeyProtectionEnum.KEY_PROTECTION_TEE.getValue());
        authenticatorDetails.matcherProtection = MatcherProtectionEnum.MATCHER_PROTECTION_TEE.getValue();
        authenticatorDetails.attachmentHint = AttachmentHintEnum.ATTACHMENT_HINT_INTERNAL.getValue();
        authenticatorDetails.isSecondFactorOnly = false;
        authenticatorDetails.tcDisplay = TCDEnum.TRANSACTION_CONFIRMATION_DISPLAY_ANY.getValue();
        authenticatorDetails.tcDisplayContentType = "text/plain";
        authenticatorDetails.tcDisplayPNGCharacteristics = null;
        authenticatorDetails.icon = "https://github.com/emersonmello/dummyuafclient/blob/master/app/src/main/res/mipmap-hdpi/ic_launcher.png";
        authenticatorDetails.supportedExtensionIDs = null;

        this.deviceCounter = mSharedPreferences.getLong("deviceCounter", 0);

    }


}