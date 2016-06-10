package br.edu.ifsc.mello.dummyuafclient.fidouaflib;

import android.util.Log;

import com.google.gson.Gson;


import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Storage;
import org.ebayopensource.fidouaf.marvin.client.config.InitConfig;
import org.ebayopensource.fidouaf.marvin.client.msg.Version;

public class DiscoveryData {
    public Version[] supportedUAFVersions;
    public String clientVendor;
    public Version clientVersion;
    public Authenticator[] availableAuthenticators;

    public static String getFakeDiscoveryData() {
        Version version = new Version(1, 0);
        DiscoveryData discoveryData = new DiscoveryData();

        discoveryData.clientVersion = version;
        discoveryData.supportedUAFVersions = new Version[1];
        discoveryData.supportedUAFVersions[0] = version;
        discoveryData.clientVendor = "DummyUAFClient Institute";
        discoveryData.availableAuthenticators = new Authenticator[1];

        if (!InitConfig.getInstance().isInitialized()) {
            try {
                InitConfig.getInstance()
                        .init(OperationalParams.AAID, OperationalParams.defaultAttestCert, OperationalParams.defaultAttestPrivKey, new OperationalParams(), new Storage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        discoveryData.availableAuthenticators[0] = InitConfig.getInstance().getOperationalParams().getAuthenticator();

        String discoveryDataJson = (new Gson()).toJson(discoveryData);
        return discoveryDataJson;
    }
}
