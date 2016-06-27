package br.edu.ifsc.mello.dummyuafclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.ebayopensource.fidouaf.marvin.ApplicationContextProvider;
import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Storage;
import org.ebayopensource.fidouaf.marvin.client.config.InitConfig;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import br.edu.ifsc.mello.dummyuafclient.fidouaflib.ErrorCode;

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
        assert fab != null;
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_details:
                Intent intent = new Intent(MainActivity.this, AttestationDetailsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
