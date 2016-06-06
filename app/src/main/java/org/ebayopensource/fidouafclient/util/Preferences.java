package org.ebayopensource.fidouafclient.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.edu.ifsc.mello.dummyuafclient.fidoauthenticator.FidoUafAuthenticator;

public class Preferences {

	private static String PREFERANCES = "Preferences";

	public static String getSettingsParam(String paramName) {
		SharedPreferences settings = getPrefferences();
		return settings.getString(paramName, "");
	}

	public static SharedPreferences getPrefferences() {

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());

		//SharedPreferences settings = ApplicationContextProvider.getContext()
		//      .getSharedPreferences(PREFERANCES, 0);
		return settings;
	}

	public static void setSettingsParam(String paramName, String paramValue) {
		SharedPreferences settings = getPrefferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(paramName, paramValue);
		editor.apply();
	}

	public static void setSettingsParamLong(String paramName, long paramValue) {
		SharedPreferences settings = getPrefferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(paramName, paramValue);
		editor.apply();
	}

}
