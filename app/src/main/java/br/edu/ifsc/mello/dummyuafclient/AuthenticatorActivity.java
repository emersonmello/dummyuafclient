package br.edu.ifsc.mello.dummyuafclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.ebayopensource.fidouaf.marvin.ApplicationContextProvider;
import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Preferences;

import java.util.ArrayList;
import java.util.Map;

public class AuthenticatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.fillListView();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        this.fillListView();

    }

    private void fillListView() {
        ArrayList<AuthenticatorInfo> arrayList = new ArrayList<>();

//        AuthenticatorInfo authenticatorInfo = new AuthenticatorInfo();
//        authenticatorInfo.setName("Test");
//        authenticatorInfo.setKeyId("KeyId teste");
//        arrayList.add(authenticatorInfo);


        arrayList = this.getAuthenticatorList();

        final AuthenticatorInfoAdapter arrayAdapter = new AuthenticatorInfoAdapter(this, arrayList);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AuthenticatorInfo authItem = (AuthenticatorInfo) listView.getItemAtPosition(position);
                final String keyId = authItem.getKeyId();
                final String appId = authItem.getName();
                new AlertDialog.Builder(AuthenticatorActivity.this)
                        .setTitle(R.string.remove_authenticator_title)
                        .setMessage(R.string.remove_authenticator_desc)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = Preferences.getPrefferences();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("appId::" + appId);
                                editor.apply();
                                OperationalParams operationalParams = new OperationalParams();
                                operationalParams.removeKey(appId);
                                arrayAdapter.clear();
                                arrayAdapter.addAll(getAuthenticatorList());
                                arrayAdapter.notifyDataSetChanged();
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
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String key = entry.getKey();
            if (key.contains("appId::")) {
                String name = key.split("::")[1];
                String keyid = ((String) entry.getValue());
                AuthenticatorInfo authenticatorInfo = new AuthenticatorInfo();
                authenticatorInfo.setName(name);
                authenticatorInfo.setKeyId(keyid);
                authenticatorInfoArrayList.add(authenticatorInfo);
            }
        }
        return authenticatorInfoArrayList;
    }

}
