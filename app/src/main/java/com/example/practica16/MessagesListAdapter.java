package com.example.practica16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessagesListAdapter extends ArrayAdapter<Mensaje> {

    private Context mContext;
    private int mResource;

    public MessagesListAdapter(Context context, int resource, List<Mensaje> objects) {
        super(context, resource, objects);

        this.mResource = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String from = getItem(position).nombre;
        String msg = getItem(position).mensaje;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView txtFrom = convertView.findViewById(R.id.txtFrom);
        TextView txtMsg = convertView.findViewById(R.id.txtMsg);

        txtFrom.setText(from);
        txtMsg.setText(msg);

        return convertView;
    }


}
