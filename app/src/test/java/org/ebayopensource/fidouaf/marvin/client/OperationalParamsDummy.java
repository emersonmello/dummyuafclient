package org.ebayopensource.fidouaf.marvin.client;

import org.ebayopensource.fidouaf.marvin.client.msg.Version;
import org.ebayopensource.fidouaf.marvin.client.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fidouaf.marvin.client.tlv.TagsEnum;

import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifsc.mello.dummyuafclient.fidouaflib.AttachmentHintEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.Authenticator;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.KeyProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.MatcherProtectionEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.TCDEnum;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.UserVerifyEnum;

public class OperationalParamsDummy implements OperationalParamsIntf{

	public static final String TEST_AAID = "TEST-AAID";

	public static final String TestKeyId = "TEST-KEYID";

	public static final byte[] TestPublicKey = "TEST_PUBLIC_KEY".getBytes();

	public static final String TestFacetId = "TEST-FACET-ID";

	public static final byte[] TestSignature = "TEST_SIGNATURE".getBytes();

	public static final byte[] TestAttestSignature = "TEST_ATTEST_SIGNATURE".getBytes();

	public static final byte[] TestAttestCert = "TEST-ATTEST-CERT".getBytes();
	
	private Map<String, RegRecord> regRecordMap = new HashMap<String, RegRecord>();

	private Authenticator authenticator;
	@Override
	public Authenticator getAuthenticator(){
		return this.authenticator;
	}

	public String getAAID() {
		return TEST_AAID;
	}

	public byte[] getAttestCert() {
		return TestAttestCert;
	}

	public long getRegCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void incrementRegCounter() {
		// TODO Auto-generated method stub
		
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

	public KeyPairGenerator getKeyPairGenerator(String keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	public RegRecord genAndRecord(String appId) {
		RegRecord r = new RegRecord(TestKeyId, TestPublicKey);
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
		// TODO Auto-generated method stub
		this.authenticator = new Authenticator();
		this.fillAuthenticatorDetails();
		this.authenticator.aaid = aaid;
		
	}

	public byte[] getSignature(byte[] signedDataValue, String keyId)
			throws Exception {
		return TestSignature;
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
