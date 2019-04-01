package com.example.practica16;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.practica16.Mensaje;
import com.example.practica16.R;

import java.util.List;

public class ContactsListAdapter extends ArrayAdapter<Usuario> {

    private Context mContext;
    private int mResource;

    public ContactsListAdapter(Context context, int resource, List<Usuario> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_adapter, parent, false);

        TextView txtUsuario = (TextView) convertView.findViewById(R.id.txtUserName);
        Usuario user = getItem(position);

        txtUsuario.setText(user.sessionId);

        return convertView;
    }
}
