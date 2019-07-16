package com.uhcl.reachapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.activities.BCCActivity;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.util.ArrayList;

public class BCCAdapter extends BaseAdapter {

    ArrayList<UserPOJO> copyOfUsersList;
    Context myContext;
    ArrayList<UserPOJO> regUsersList;
    LayoutInflater inflater;

    public BCCAdapter(BCCActivity bccActivity, int list_item, ArrayList<UserPOJO> objects) {
        this.myContext = bccActivity;
        inflater = LayoutInflater.from(myContext);

        regUsersList = objects;
        copyOfUsersList = new ArrayList<>();
        copyOfUsersList.addAll(regUsersList);
    }

    public class ViewHolder {
        TextView username;
        ImageView profilePic;
    }

    @Override
    public int getCount() {
        return regUsersList.size();
    }

    @Override
    public Object getItem(int position) {
        return regUsersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_user, null);
            holder.username = convertView.findViewById(R.id.tv_listitem_username);
            holder.profilePic = convertView.findViewById(R.id.iv_listitem_userprofilepic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.username.setText(regUsersList.get(position).getContactname_inphone());
        Glide.with(myContext).load(regUsersList.get(position).getProfilepic_firebaseuri()).placeholder(R.drawable.baseline_person_black_24).fitCenter().into(holder.profilePic);

        return convertView;
    }
}
