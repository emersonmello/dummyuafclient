package br.edu.ifsc.mello.dummyuafclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        TextView name = (TextView) convertView.findViewById(R.id.auth_name);
        TextView created = (TextView) convertView.findViewById(R.id.auth_created);
        TextView keyid = (TextView) convertView.findViewById(R.id.auth_keyid);

        if (authenticatorInfo.getName() != null) {
            name.setText(authenticatorInfo.getName());
        }
        if (authenticatorInfo.getCreated() != null){
            created.setText(DateFormat.getDateTimeInstance().format(authenticatorInfo.getCreated()));
        }
        if (authenticatorInfo.getKeyId() != null) {
            keyid.setText(authenticatorInfo.getKeyId());
        }
        return convertView;
    }
}
