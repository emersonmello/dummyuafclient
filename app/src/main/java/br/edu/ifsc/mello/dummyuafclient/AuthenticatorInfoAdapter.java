package br.edu.ifsc.mello.dummyuafclient;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

public class AuthenticatorInfoAdapter extends ArrayAdapter<AuthenticatorInfo> {

    public AuthenticatorInfoAdapter(Context context, ArrayList<AuthenticatorInfo> authenticatorInfos){
        super(context,0,authenticatorInfos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AuthenticatorInfo authenticatorInfo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_authenticator, parent, false);
        }

        convertView.setTag(authenticatorInfo);

        TextView appId = (TextView) convertView.findViewById(R.id.auth_appid);
        TextView keyid = (TextView) convertView.findViewById(R.id.auth_keyid);
        TextView pubKey = (TextView) convertView.findViewById(R.id.auth_publickey);
        TextView secureHw = (TextView) convertView.findViewById(R.id.secure_hw_status);

        if (authenticatorInfo.getAppId() != null) {
            appId.setText(authenticatorInfo.getAppId());
        }
        if (authenticatorInfo.getKeyId() != null) {
            keyid.setText(authenticatorInfo.getKeyId());
        }

        if (authenticatorInfo.getPubKey() != null){
            pubKey.setText(authenticatorInfo.getPubKey());
        }

        if (authenticatorInfo.isSecureHw()){
            secureHw.setText(R.string.private_key_is_sec_hw);
            secureHw.setTextColor(ContextCompat.getColor(getContext(),R.color.success_color));
        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
