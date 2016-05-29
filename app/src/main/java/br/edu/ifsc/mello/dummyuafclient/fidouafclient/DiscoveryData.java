package br.edu.ifsc.mello.dummyuafclient.fidouafclient;

import com.google.gson.Gson;

import org.ebayopensource.fido.uaf.msg.Version;
import org.ebayopensource.fido.uaf.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fido.uaf.tlv.TagsEnum;

public class DiscoveryData {
    public Version[] supportedUAFVersions;
    public String clientVendor;
    public Version clientVersion;
    public Authenticator[] availableAuthenticators;

    public static String getFakeDiscoveryData() {
        Version version = new Version(1, 0);

        Authenticator authenticator = new Authenticator();
        authenticator.title = "Dummy UAF Client";
        authenticator.aaid = "DMY0#0001";
        authenticator.description = "A dummy UAF Client suitable to conduct development tests on smartphones that are not FIDO Ready.";
        authenticator.supportedUAFVersions = new Version[1];
        authenticator.supportedUAFVersions[0] = version;
        authenticator.assertionScheme = "UAFV1TLV";
        authenticator.authenticationAlgorithm = (short) AlgAndEncodingEnum.UAF_ALG_SIGN_SECP256R1_ECDSA_SHA256_RAW.id;
        authenticator.attestationTypes = new short[1];
        authenticator.attestationTypes[0] = (short) TagsEnum.TAG_ATTESTATION_BASIC_FULL.id;
        authenticator.userVerification = UserVerifyEnum.USER_VERIFY_FINGERPRINT.getValue();
        authenticator.keyProtection = KeyProtectionEnum.KEY_PROTECTION_HARDWARE.getValue();

        authenticator.matcherProtection = MatcherProtectionEnum.MATCHER_PROTECTION_TEE.getValue();
        authenticator.attachmentHint = AttachmentHintEnum.ATTACHMENT_HINT_INTERNAL.getValue();
        authenticator.isSecondFactorOnly = false;
        authenticator.tcDisplay = TCDEnum.TRANSACTION_CONFIRMATION_DISPLAY_ANY.getValue();
        authenticator.tcDisplayContentType = "text/plain";
        authenticator.tcDisplayPNGCharacteristics = null;
        authenticator.icon = "http://";
        authenticator.supportedExtensionIDs = null;


        DiscoveryData discoveryData = new DiscoveryData();

        discoveryData.clientVersion = version;
        discoveryData.supportedUAFVersions = new Version[1];
        discoveryData.supportedUAFVersions[0] = version;
        discoveryData.clientVendor = "DummyUAFClient Institute";
        discoveryData.availableAuthenticators = new Authenticator[1];
        discoveryData.availableAuthenticators[0] = authenticator;


        String discoveryDataJson = (new Gson()).toJson(discoveryData);
        return discoveryDataJson;
    }
}
