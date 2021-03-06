package org.ebayopensource.fidouaf.marvin.client;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.fidouaf.marvin.client.crypto.SHA;
import org.ebayopensource.fidouaf.marvin.client.msg.Version;
import org.ebayopensource.fidouaf.marvin.client.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fidouaf.marvin.client.tlv.TagsEnum;

import br.edu.ifsc.mello.dummyuafclient.fidouaflib.AttachmentHintEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.Authenticator;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.KeyProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.MatcherProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.TCDEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.UserVerifyEnum;

public class OperationalParams implements OperationalParamsIntf{
	
	private int regCounter = 0;

	public static final String TEST_AAID = "TEST-AAID";

	public static final String TestKeyId = "TEST-KEYID";

	public static final String TestFacetId = "TEST-FACET-ID";

	public static final byte[] TestAttestSignature = "TEST_ATTEST_SIGNATURE".getBytes();

	public static final byte[] TestAttestCert = "TEST-ATTEST-CERT".getBytes();
	
	private Map<String, RegRecord> regRecordMap = new HashMap<String, RegRecord>();
	private Map<String, PrivateKey> dummyKeyStore = new HashMap<String, PrivateKey>();

	public String getAAID() {
		return TEST_AAID;
	}

	public byte[] getAttestCert() {
		return TestAttestCert;
	}

	public long getRegCounter() {
		return regCounter;
	}

	public void incrementRegCounter() {
		regCounter++;
	}

	private Authenticator authenticator;
	@Override
	public Authenticator getAuthenticator(){
		return this.authenticator;
	}

	public long getAuthCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void incrementAuthCounter() {
		// TODO Auto-generated method stub
		
	}

	public boolean isFacetIdValid(String appId, String facetId) {
		return true;
	}

	public byte[] signWithAttestationKey(byte[] dataToSign) throws Exception {
		return TestAttestSignature;
	}

	public StorageInterface getStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	public KeyPairGenerator getKeyPairGenerator(String keyId)  {
		

		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("EC");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		 ECGenParameterSpec kpgparams = new ECGenParameterSpec("secp256r1");
		 try {
			keyPairGenerator.initialize(kpgparams);
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}
//		 
//		 KeyPair pair = keyPairGenerator.generateKeyPair();
//		 
//		 try{
//         // Instance of signature class with SHA256withECDSA algorithm
//         Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
//         ecdsaSign.initSign(pair.getPrivate());
//
//         System.out.println("Private Keys is::" + pair.getPrivate());
//         System.out.println("Public Keys is::" + pair.getPublic());
//
//         String msg = "text ecdsa with sha256";//getSHA256(msg)
//         byte[] dataForSigning = SHA.sha(msg.getBytes(), "SHA-256");
//         ecdsaSign.update(dataForSigning);
//
//         byte[] signature = ecdsaSign.sign();
//         System.out.println("Signature is::"
//                 + new BigInteger(1, signature).toString(16));
//
//         // Validation
//         ecdsaSign.initVerify(pair.getPublic());
//         ecdsaSign.update(dataForSigning);
//         if (ecdsaSign.verify(signature))
//             System.out.println("valid");
//         else
//             System.out.println("invalid!!!!");
//		 } catch (Exception e){
//			 e.printStackTrace();
//		 }

		return keyPairGenerator;
	}

	public RegRecord genAndRecord(String appId) {
		KeyPair keyPair = getKeyPairGenerator(TestKeyId).generateKeyPair();
		/***
		 * This is just an example. 
		 * Real KeyStore, like "AndroidKeyStore" implementation should be used
		 */
		dummyKeyStore.put(getKeyId(appId), keyPair.getPrivate());
		RegRecord r = new RegRecord(TestKeyId, keyPair.getPublic().getEncoded());
		regRecordMap.put(appId, r);
		return r;
	}

	public String getFacetId(String appId) {
		return TestFacetId;
	}

	public String getKeyId(String appId) {
		return TestKeyId;
	}

	public void init(String aaid, byte[] attestCert, byte[] attestPrivKey,
			StorageInterface storage) {
		this.authenticator = new Authenticator();
		this.fillAuthenticatorDetails();
		this.authenticator.aaid = aaid;
	}

	public byte[] getSignature(byte[] signedDataValue, String keyId)
			throws Exception {
		PrivateKey priv = dummyKeyStore.get(keyId);
		Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(priv);
        byte[] dataForSigning = SHA.sha(signedDataValue, "SHA-256");
        ecdsaSign.update(dataForSigning);
        byte[] signature = ecdsaSign.sign();
		return signature;
	}
	
	public static void main(String[] args) throws Exception {
		OperationalParams obj = new OperationalParams();
		obj.getKeyPairGenerator("KeyId");
		
		obj.genAndRecord("TestAppId");
		obj.getSignature("signedDataValue".getBytes(), TestKeyId);
		
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
