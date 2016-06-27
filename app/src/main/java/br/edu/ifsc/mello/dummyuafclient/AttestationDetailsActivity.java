package br.edu.ifsc.mello.dummyuafclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ebayopensource.fidouaf.marvin.ApplicationContextProvider;
import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Preferences;
import org.ebayopensource.fidouaf.marvin.Storage;
import org.ebayopensource.fidouaf.marvin.client.config.InitConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class AttestationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attestation_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fillView();
    }

    private void fillView() {
        TextView textView = (TextView) findViewById(R.id.textviewCertificate);
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
            String certEncoded = settings.getString(ApplicationContextProvider.ATTEST_KEY + "_CERT", "");
            byte[] certDecoded = android.util.Base64.decode(certEncoded.getBytes(), Base64.DEFAULT);
            InputStream in = new ByteArrayInputStream(certDecoded);
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(in);

            if (certificate != null) {
//                int version = certificate.getVersion();
//                String serial = certificate.getSerialNumber().toString(16);
//                String issuerDN = certificate.getIssuerDN().toString();
//                String subjectDn = certificate.getSubjectDN().toString();
//                Date begin = certificate.getNotBefore();
//                Date end = certificate.getNotAfter();
//                String signAlg = certificate.getSigAlgName();
//                PublicKey pk = certificate.getPublicKey();
//                if (pk != null) {
//                    StringBuilder builder = new StringBuilder();
//                    String pubKey = certificate.getPublicKey().getAlgorithm();
//                    byte[] pkenc = pk.getEncoded();
//                    for (int i = 0; i < pkenc.length; i++) {
//                        builder.append(pkenc[i]);
//                    }
//                    String p = builder.toString();
//                }
//                BigInteger b = new BigInteger(certificate.getSignature());
//                String sign = b.toString(16);


                textView.setText(certificate.toString());
            }
        } catch (Exception e) {
            Log.d("AttestAct", "error: " + e.toString());
        }
    }

    public void generateNewAttestKey(View view) {
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.reset_attest_title)
                    .setIcon(R.drawable.ic_error_outline_black_24dp)
                    .setMessage(R.string.reset_attest_desc)
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
                                    ks.deleteEntry(alias);
                                }
                                byte[] certificate = ApplicationContextProvider.generateAttestationKey(true);
                                InitConfig.getInstance()
                                        .init(OperationalParams.AAID, OperationalParams.defaultAttestCert, certificate, new OperationalParams(), new Storage());
                                fillView();
                            } catch (Exception e) {
                                Log.d("AttestAct", "Reset error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, null).show();
        }

    }


}
