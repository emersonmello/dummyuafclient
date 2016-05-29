package org.ebayopensource.fidouafclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Endpoints {

	public static final String SERVER =
			"http://10.0.2.2:8123";
	//"http://openidconnect.ebay.com";
	//"http://www.head2toes.org";
	public static final String GET_AUTH_REQUEST = "/fidouaf/v1/public/authRequest";
	public static final String POST_AUTH_RESPONSE = "/fidouaf/v1/public/authResponse";
	public static final String POST_DEREG_RESPONSE = "/fidouaf/v1/public/deregRequest";
	public static final String GET_REG_REQUEST = "/fidouaf/v1/public/regRequest/";
	public static final String POST_REG_RESPONSE = "/fidouaf/v1/public/regResponse";


	private static String check(String key) {
		Context context = ApplicationContextProvider.getContext();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

		return sharedPref.getString(key,"");

//        String serverEndpoint = Preferences.getSettingsParam("serverEndpoint");
//        if (serverEndpoint != null && serverEndpoint.length() == 0) {
//            setDefaults();
//        }

	}

	public static String getServer() {
		return check("fido_server_endpoint");
		//return Preferences.getSettingsParam("serverEndpoint");
	}


	public static String getAuthResponseEndpoint() {
		return getServer() + check("fido_auth_response");
		//return getServer() + Preferences.getSettingsParam("authRes");
	}


	public static String getAuthRequestEndpoint() {
		return getServer() + check("fido_auth_request");
		//return getServer() + Preferences.getSettingsParam("authReg");
	}


	public static String getDeregEndpoint() {
		return getServer() + check("fido_dereg_request");
		//return getServer() + Preferences.getSettingsParam("dereg");
	}


	public static String getRegResponseEndpoint() {
		return getServer() + check("fido_reg_response");
		//return getServer() + Preferences.getSettingsParam("regRes");
	}


	public static String getRegRequestEndpoint() {
		return getServer() + check("fido_reg_request") + "/";
		//return getServer() + Preferences.getSettingsParam("regReg");
	}

	//Path
	public static String getAuthResponsePath() {
		return check("fido_auth_response");
		//return Preferences.getSettingsParam("authRes");
	}


	public static String getAuthRequestPath() {
		return check("authReg");
		//return Preferences.getSettingsParam("authReg");
	}


	public static String getDeregPath() {
		return check("dereg");
		//return Preferences.getSettingsParam("dereg");
	}


	public static String getRegResponsePath() {
		return check("regRes");
		//return Preferences.getSettingsParam("regRes");
	}


	public static String getRegRequestPath() {
		return check("regReg");
		//return Preferences.getSettingsParam("regReg");
	}

	public static void setDefaults() {
		Preferences.setSettingsParam("serverEndpoint", SERVER);
		Preferences.setSettingsParam("authReg", GET_AUTH_REQUEST);
		Preferences.setSettingsParam("authRes", POST_AUTH_RESPONSE);
		Preferences.setSettingsParam("regReg", GET_REG_REQUEST);
		Preferences.setSettingsParam("regRes", POST_REG_RESPONSE);
		Preferences.setSettingsParam("dereg", POST_DEREG_RESPONSE);
	}


	public static void save(String server, String authReq, String authRes,
							String regReq, String regRes, String dereg) {
		Preferences.setSettingsParam("serverEndpoint", server);
		Preferences.setSettingsParam("authReg", authReq);
		Preferences.setSettingsParam("authRes", authRes);
		Preferences.setSettingsParam("regReg", regReq);
		Preferences.setSettingsParam("regRes", regRes);
		Preferences.setSettingsParam("dereg", dereg);
	}
}
