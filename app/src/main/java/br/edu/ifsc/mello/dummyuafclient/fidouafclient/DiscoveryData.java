package br.edu.ifsc.mello.dummyuafclient.fidouafclient;

import android.content.Context;

import com.google.gson.Gson;

import org.ebayopensource.fido.uaf.msg.Version;
import org.ebayopensource.fido.uaf.tlv.AlgAndEncodingEnum;
import org.ebayopensource.fido.uaf.tlv.TagsEnum;

import br.edu.ifsc.mello.dummyuafclient.fidoauthenticator.FidoUafAuthenticator;

public class DiscoveryData {
    public Version[] supportedUAFVersions;
    public String clientVendor;
    public Version clientVersion;
    public Authenticator[] availableAuthenticators;

    public static String getFakeDiscoveryData() {
        Version version = new Version(1, 0);

        FidoUafAuthenticator fidoUafAuthenticator = new FidoUafAuthenticator();

        DiscoveryData discoveryData = new DiscoveryData();

        discoveryData.clientVersion = version;
        discoveryData.supportedUAFVersions = new Version[1];
        discoveryData.supportedUAFVersions[0] = version;
        discoveryData.clientVendor = "DummyUAFClient Institute";
        discoveryData.availableAuthenticators = new Authenticator[1];
        discoveryData.availableAuthenticators[0] = fidoUafAuthenticator.getAuthenticatorDetails();


        String discoveryDataJson = (new Gson()).toJson(discoveryData);
        return discoveryDataJson;
    }
}
