package br.edu.ifsc.mello.dummyuafclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ebayopensource.fidouaf.marvin.ApplicationContextProvider;
import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Preferences;
import org.ebayopensource.fidouaf.marvin.Storage;
import org.ebayopensource.fidouaf.marvin.client.StorageInterface;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

public class AuthenticatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fillListView();
    }

    private void showListView(AuthenticatorInfoAdapter arrayAdapter, ListView listView, TextView textView) {
        listView.setVisibility((arrayAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
        textView.setVisibility((arrayAdapter.isEmpty()) ? View.VISIBLE : View.GONE);
    }

    private void fillListView() {
        ArrayList<AuthenticatorInfo> arrayList = this.getAuthenticatorList();

        final AuthenticatorInfoAdapter arrayAdapter = new AuthenticatorInfoAdapter(this, arrayList);
        final ListView listView = (ListView) findViewById(R.id.listView);
        final TextView textView = (TextView) findViewById(R.id.textview_empty);
        listView.setAdapter(arrayAdapter);

        showListView(arrayAdapter, listView, textView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AuthenticatorInfo authItem = (AuthenticatorInfo) listView.getItemAtPosition(position);
                final String appId = authItem.getAppId();
                new AlertDialog.Builder(AuthenticatorActivity.this)
                        .setTitle(R.string.remove_authenticator_title)
                        .setMessage(R.string.remove_authenticator_desc)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Removing key from keyStore
                                OperationalParams operationalParams = new OperationalParams();
                                operationalParams.removeKey(appId);

                                // Removing details appId details from SharedPreferences
                                SharedPreferences sharedPreferences = Preferences.getPrefferences();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("appId::" + appId);
                                editor.apply();
                                arrayAdapter.clear();
                                arrayAdapter.addAll(getAuthenticatorList());
                                arrayAdapter.notifyDataSetChanged();
                                showListView(arrayAdapter, listView, textView);
                            }
                        })
                        .setNegativeButton(R.string.no, null).show();
            }
        });
    }

    public ArrayList<AuthenticatorInfo> getAuthenticatorList() {
        ArrayList<AuthenticatorInfo> authenticatorInfoArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = Preferences.getPrefferences();
        Map<String, ?> all = sharedPreferences.getAll();
        if (!all.isEmpty()) {
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.contains("appId::")) {
                    String appId = key.split("::")[1];
                    String keyid = ((String) entry.getValue());
                    AuthenticatorInfo authenticatorInfo = new AuthenticatorInfo();
                    authenticatorInfo.setAppId(appId);
                    authenticatorInfo.setKeyId(keyid);
                    this.getPublicKey(keyid, authenticatorInfo);
                    authenticatorInfoArrayList.add(authenticatorInfo);
                }
            }
        } else {
            Log.d("authList", "SharedPreferences is empty");
        }
        return authenticatorInfoArrayList;
    }

    private String getPublicKey(String alias, AuthenticatorInfo authenticatorInfo) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry keyEntry = ks.getEntry(alias, null);
            if (keyEntry instanceof KeyStore.PrivateKeyEntry) {
                PrivateKey pk = ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey();
                KeyFactory factory = KeyFactory.getInstance(pk.getAlgorithm(), "AndroidKeyStore");
                KeyInfo keyInfo = (KeyInfo) factory.getKeySpec(pk, KeyInfo.class);
                Certificate certificate = ks.getCertificate(alias);
                PublicKey publicKey = certificate.getPublicKey();
                String pkString = Base64.encodeToString(publicKey.getEncoded(), android.util.Base64.DEFAULT);
                if (authenticatorInfo != null) {
                    authenticatorInfo.setPubKey(pkString);
                    if (keyInfo.isInsideSecureHardware()) {
                        authenticatorInfo.setSecureHw(true);
                    }
                }
                return pkString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showPubKey(View view) {
        TextView textView = (TextView) findViewById(R.id.auth_keyid);
        String keyid = textView.getText().toString();
        String pubKey = getPublicKey(keyid, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.pubkey_title)
                .setMessage(pubKey)
                .setNeutralButton(R.string.button_done, null).show();

    }

}
