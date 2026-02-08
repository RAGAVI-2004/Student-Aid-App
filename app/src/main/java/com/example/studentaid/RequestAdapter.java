package com.example.studentaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class RequestAdapter extends ArrayAdapter<RoommateRequest> {

    public RequestAdapter(Context context, List<RoommateRequest> requests) {
        super(context, 0, requests);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_request, parent, false);
        }

        RoommateRequest req = getItem(position);

        if (req != null) {
            ((TextView) convertView.findViewById(R.id.tvName)).setText("Name: " + req.name);
            ((TextView) convertView.findViewById(R.id.tvGender)).setText("Gender: " + req.gender);
            ((TextView) convertView.findViewById(R.id.tvClass)).setText("Class: " + req.studentClass);
            ((TextView) convertView.findViewById(R.id.tvRoom)).setText("Room: " + req.room);
            ((TextView) convertView.findViewById(R.id.tvMembers)).setText("Members: " + req.members);
            ((TextView) convertView.findViewById(R.id.tvEmail)).setText("Email: " + req.email);
            ((TextView) convertView.findViewById(R.id.tvDesc)).setText("Desc: " + req.desc);
        }

        return convertView;
    }
}
