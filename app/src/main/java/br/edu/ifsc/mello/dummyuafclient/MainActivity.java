package br.edu.ifsc.mello.dummyuafclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;

import org.ebayopensource.fidouaf.marvin.Preferences;

import java.security.KeyStore;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FIDOUAFClientActivity.class);
                startActivity(intent);
            }
        });

    }


    public void showAuthListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, AuthenticatorActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_reset:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.reset_title)
                        .setIcon(R.drawable.ic_error_outline_black_24dp)
                        .setMessage(R.string.reset_desc)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = Preferences.getPrefferences();
                                SharedPreferences.Editor editor = settings.edit();
                                editor.clear();
                                editor.apply();
                                try {
                                    KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                                    ks.load(null);
                                    Enumeration<String> aliases = ks.aliases();
                                    while (aliases.hasMoreElements()) {
                                        String alias = aliases.nextElement();
                                        if (!alias.contains("UAFAttestKey")){
                                            ks.deleteEntry(alias);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
