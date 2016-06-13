package br.edu.ifsc.mello.dummyuafclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ebayopensource.fidouaf.marvin.client.msg.Version;
import org.ebayopensource.fidouaf.marvin.client.msg.client.UAFIntentType;
import org.ebayopensource.fidouaf.marvin.client.op.Auth;
import org.ebayopensource.fidouaf.marvin.client.op.Dereg;
import org.ebayopensource.fidouaf.marvin.OperationalParams;
import org.ebayopensource.fidouaf.marvin.Preferences;
import org.ebayopensource.fidouaf.marvin.Storage;
import org.ebayopensource.fidouaf.marvin.client.config.InitConfig;
import org.ebayopensource.fidouaf.marvin.client.op.Reg;
import org.json.JSONObject;

import br.edu.ifsc.mello.dummyuafclient.fidouaflib.Curl;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.DiscoveryData;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.ErrorCode;
import br.edu.ifsc.mello.dummyuafclient.fidouaflib.FidoUafUtils;


import static br.edu.ifsc.mello.dummyuafclient.fidouaflib.ErrorCode.NO_ERROR;

public class FIDOUAFClientActivity extends AppCompatActivity implements FingerprintUiHelper.Callback {

    public static final int MY_PERMISSIONS_USE_FINGERPRINT = 1;
    private Button mCancelButton;
    private View mFingerprintContent;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerprintUiHelper.FingerprintUiHelperBuilder mFingerprintUiHelperBuilder;
    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerprintManager;
    private GetTrustedFacetsTask mGetTrustedTask;
    private ProgressBar mProgressBar;
    private Intent callingIntent;
    private String appFacetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_dialog_container);

        mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        mFingerprintContent = this.findViewById(R.id.fingerprint_container);
        mCancelButton = (Button) this.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uafError(ErrorCode.USER_CANCELLED.getID(), null);
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mFingerprintUiHelperBuilder = new FingerprintUiHelper.FingerprintUiHelperBuilder(mFingerprintManager);

        mFingerprintUiHelper = mFingerprintUiHelperBuilder.build(
                (ImageView) this.findViewById(R.id.fingerprint_icon),
                (TextView) this.findViewById(R.id.fingerprint_status), this);

        // Are you using a user authentication? No? I'm sorry, you have to.
        if (!mKeyguardManager.isKeyguardSecure()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.screenlock)
                    .setMessage(R.string.screenlock_msg)
                    .setPositiveButton(R.string.button_go_to_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                        }
                    }).show();
        } else {
            // If fingerprint authentication is not available
            if (!mFingerprintUiHelper.isFingerprintAuthAvailable(this)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.fingerprint_not_enrolled)
                        .setMessage(R.string.fingerprint_not_enrolled_msg)
                        .setPositiveButton(R.string.button_go_to_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                            }
                        }).show();
            } else {
                mFingerprintContent.setVisibility(View.VISIBLE);
            }
        }
        this.callingIntent = getIntent();
        this.init();
        final ProgressBar countdownPB = (ProgressBar) findViewById(R.id.countdown_progress_bar);
        countdownPB.setProgress(100);
        new CountDownTimer(10000, 100) {
            public void onTick(long millisUntilFinished) {
                countdownPB.setProgress(countdownPB.getProgress() - 1);
            }

            public void onFinish() {
                uafError(ErrorCode.USER_CANCELLED.getID(), null);
            }
        }.start();
    }

    private void init() {
        if (!InitConfig.getInstance().isInitialized()) {
            try {
                InitConfig.getInstance()
                        .init(OperationalParams.AAID, OperationalParams.defaultAttestCert, OperationalParams.defaultAttestPrivKey, new OperationalParams(), new Storage());
            } catch (Exception e) {
                Log.i("FIDOUAFClient", "Key generator init failed");
                this.uafError(ErrorCode.UNKNOWN.getID(), null);
            }
        }
    }


    private void uafError(short errorCode, String uafintentType) {
        if (callingIntent != null) {
            Bundle bundle = new Bundle();
            String response = "";
            if (uafintentType != null) {
                bundle.putString("UAFIntentType", uafintentType);
            }
            bundle.putShort("errorCode", errorCode);
            bundle.putString("message", response);
            callingIntent.putExtras(bundle);
            setResult(Activity.RESULT_CANCELED, callingIntent);
        }
        mGetTrustedTask = null;
        finish();
    }

    @Override
    public void onError() {
        uafError(ErrorCode.UNKNOWN.getID(), null);
        showProgress(false);
    }

    @Override
    public void onAuthenticated() {
        this.processUAFIntentType(getIntent());
    }

    public void processUAFIntentType(Intent intent) {
        this.callingIntent = intent;

        String callingPackageName = getPackageName();
        Preferences.setSettingsParam("callingPackageName", callingPackageName);

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String data = (String) extras.get("UAFIntentType");
            if (data != null) {
                if (data.equals(UAFIntentType.DISCOVER.name())) {
                    extras = new Bundle();
                    extras.putString("UAFIntentType", UAFIntentType.DISCOVER_RESULT.name());
                    extras.putShort("errorCode", NO_ERROR.getID());
                    extras.putString("discoveryData", DiscoveryData.getFakeDiscoveryData());
                    intent.putExtras(extras);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                if (data.equals(UAFIntentType.UAF_OPERATION.name())) {
                    String message = (String) extras.get("message");
                    //TODO According to FIDO Protocol, it contains TLS information to be sent by the FIDO client to the FIDO Server
                    String channelBindings = (String) extras.get("channelBindings");
                    String inMsg = extract(message);

                    if (inMsg.isEmpty()) {
                        uafError(ErrorCode.PROTOCOL_ERROR.getID(), UAFIntentType.UAF_OPERATION_RESULT.name());
                        return;
                    }

                    if (inMsg.contains("\"Dereg\"")) {
                        try {
                            Dereg deregOp = new Dereg();
                            String response = deregOp.dereg(inMsg);
                            extras.putShort("errorCode", ErrorCode.NO_ERROR.getID());
                            extras.putString("message", response);
                            callingIntent.putExtras(extras);
                            setResult(Activity.RESULT_OK, callingIntent);
                            finish();
                        } catch (Exception e) {
                            Log.i("FIDOUAFClient", "processOp failed. e=" + e);
                            uafError(ErrorCode.UNKNOWN.getID(), null);
                        }
                    } else {
                        OperationalParams operationalParams = new OperationalParams();
                        appFacetId = operationalParams.getFacetId();
                        String appId = FidoUafUtils.extractAppId(inMsg);

                        if (appFacetId == null){
                            Log.i("FIDOUAFClient", "processOp failed.");
                            uafError(ErrorCode.UNKNOWN.getID(), null);
                            return;
                        }
                        if (appId.isEmpty()) {
                            this.executeFIDOOperations(inMsg, appFacetId, false);
                        } else if (appId.contains(appFacetId)) {
                            inMsg = FidoUafUtils.updateAppId(inMsg, appFacetId);
                            this.executeFIDOOperations(inMsg, appFacetId, false);
                        } else {
                            showProgress(true);
                            mGetTrustedTask = new GetTrustedFacetsTask();
                            mGetTrustedTask.execute(appId, inMsg);
                        }
                    }
                }
            }
        } else {
            finish();
        }
    }


    private void executeFIDOOperations(String inMsg, String trustedFacets, boolean getAppId) {
        if (trustedFacets.isEmpty()) {
            uafError(ErrorCode.PROTOCOL_ERROR.getID(), UAFIntentType.UAF_OPERATION_RESULT.name());

            return;
        }

        Bundle extras = new Bundle();
        extras.putString("UAFIntentType", UAFIntentType.UAF_OPERATION_RESULT.name());

        String response = "";
        if (getAppId) {
            if (!FidoUafUtils.isFacetIdValid(trustedFacets, new Version(1, 0), appFacetId)) {
                extras.putShort("errorCode", ErrorCode.UNTRUSTED_FACET_ID.getID());
                extras.putString("message", response);
                callingIntent.putExtras(extras);
                setResult(Activity.RESULT_CANCELED, callingIntent);
                finish();
            }
        }

        try {
            if (inMsg.contains("\"Reg\"")) {
                Reg regOp = new Reg();
                response = regOp.register(inMsg);
            } else if (inMsg.contains("\"Auth\"")) {
                Auth authOp = new Auth();
                response = authOp.auth(inMsg);
            }
            extras.putShort("errorCode", NO_ERROR.getID());
            extras.putString("message", response);
            callingIntent.putExtras(extras);
            setResult(Activity.RESULT_OK, callingIntent);
            finish();
        } catch (Exception e) {
            Log.i("FIDOUAFClient", "processOp failed. e=" + e);
            uafError(ErrorCode.UNKNOWN.getID(), null);
        }

    }

    private String extract(String inMsg) {
        try {
            JSONObject tmpJson = new JSONObject(inMsg);
            String uafMsg = tmpJson.getString("uafProtocolMessage");
            uafMsg.replace("\\\"", "\"");
            return uafMsg;
        } catch (Exception e) {
            //TODO LOG IT
            return "";
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
        uafError(ErrorCode.USER_CANCELLED.getID(), null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_USE_FINGERPRINT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.onCreate(null);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.requested_permission)
                            .setMessage(R.string.fingerprint_permission)
                            .setCancelable(true)
                            .setNeutralButton(R.string.button_done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
                return;
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // We can't hang user interface, so do get appID in a different thread.
    public class GetTrustedFacetsTask extends AsyncTask<String, Void, String> {
        private String result;
        private String inMsg;

        @Override
        protected String doInBackground(String... params) {
            this.inMsg = params[1];
            result = Curl.get(params[0]).getPayload();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            mGetTrustedTask = null;
            this.result = result;
            showProgress(false);
            executeFIDOOperations(this.inMsg, this.result, true);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            mGetTrustedTask = null;
            showProgress(false);
            uafError(ErrorCode.USER_CANCELLED.getID(), null);
        }

        @Override
        protected void onCancelled() {
            mGetTrustedTask = null;
            showProgress(false);
            uafError(ErrorCode.USER_CANCELLED.getID(), null);
        }
    }
}
