package br.edu.ifsc.mello.dummyuafclient.fidouaflib;

import com.google.gson.Gson;

import org.ebayopensource.fidouaf.marvin.client.msg.Version;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mello on 07/06/16.
 */
public abstract class FidoUafUtils {

    public static String extractAppId(String serverResponse) {
        JSONArray requestArray = null;
        String appID = "";
        try {
            requestArray = new JSONArray(serverResponse);
            appID = ((JSONObject) requestArray.get(0)).getJSONObject("header").getString("appID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appID;
    }

    public static String updateAppId(String serverResponse, String facetIdList) {
        String appId = extractAppId(serverResponse);
        if (appId.isEmpty()) {
            try {
                JSONArray requestArray = new JSONArray(serverResponse);
                String facetId = (facetIdList.split(",").length > 1) ? facetIdList.split(",")[0] : facetIdList;
                ((JSONObject) requestArray.get(0)).getJSONObject("header").put("appID", facetId);
                return requestArray.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serverResponse;
    }


    /**
     * From among the objects in the trustedFacet array, select the one with the version matching
     * that of the protocol message version. The scheme of URLs in ids MUST identify either an
     * application identity (e.g. using the apk:, ios: or similar scheme) or an https: Web Origin [RFC6454].
     * Entries in ids using the https:// scheme MUST contain only scheme, host and port components,
     * with an optional trailing /. Any path, query string, username/password, or fragment information
     * MUST be discarded.
     *
     * @param trustedFacetsJson
     * @param version
     * @param appFacetId
     * @return true if appID list contains facetId (current Android application's signature).
     */
    public static boolean isFacetIdValid(String trustedFacetsJson, Version version, String appFacetId) {
        try {
            TrustedFacetsList trustedFacetsList = (new Gson()).fromJson(trustedFacetsJson, TrustedFacetsList.class);
            for (TrustedFacets trustedFacets : trustedFacetsList.getTrustedFacets()) {
                // select the one with the version matching that of the protocol message version
                if ((trustedFacets.getVersion().minor >= version.minor)
                        && (trustedFacets.getVersion().major <= version.major)) {
                    //The scheme of URLs in ids MUST identify either an application identity
                    // (e.g. using the apk:, ios: or similar scheme) or an https: Web Origin [RFC6454].
                    String[] searchHelper = appFacetId.split(",");
                    for (String facetId : searchHelper) {
                        for (String id : trustedFacets.getIds()) {
                            if (id.equals(facetId)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAppIdEqualsFacetId(String appFacetIds, String appID) {
        String[] searchHelper = appFacetIds.split(",");
        for (String facet : searchHelper) {
            if (facet.equals(appID)) {
                return true;
            }
        }
        return false;
    }
}
