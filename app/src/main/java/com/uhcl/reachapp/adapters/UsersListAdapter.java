package com.uhcl.reachapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.uhcl.reachapp.R;
import com.uhcl.reachapp.activities.ChatScreenActivity;
import com.uhcl.reachapp.data_models.UserPOJO;

import java.util.ArrayList;
import java.util.Locale;

public class UsersListAdapter extends ArrayAdapter<UserPOJO> {

    ArrayList<UserPOJO> usersList = null;
    ArrayList<UserPOJO> copyOfUsersList = null;
    Context myContext;
    LayoutInflater inflater;
    public static final int ACTIVITY_REQUEST_CHATSCREEN = 50;

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UserPOJO> objects) {
        super(context, resource, objects);
        this.myContext = context;
        inflater = LayoutInflater.from(myContext);
        usersList = objects;
        copyOfUsersList = new ArrayList<>();
        copyOfUsersList.addAll(usersList);
    }

    public class ViewHolder {
        TextView username;
        ImageView profilePic;
    }

    @Override
    public UserPOJO getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return usersList.size();
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

        if (usersList.get(position).getIsBroadcast().equalsIgnoreCase("YES")) {
            holder.username.setText(usersList.get(position).getSingle_group_name());
            holder.profilePic.setImageResource(R.drawable.baseline_group_black_24);
        } else {
            holder.username.setText(usersList.get(position).getContactname_inphone());
            Glide.with(myContext).load(usersList.get(position).getProfilepic_firebaseuri()).placeholder(R.drawable.baseline_person_black_24).fitCenter().into(holder.profilePic);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext, ChatScreenActivity.class);
                intent.putExtra("isBroadcast", usersList.get(position).getIsBroadcast());
                intent.putExtra("ChatWithUserObj", usersList.get(position));
//                myContext.startActivity(intent);
                ((Activity) myContext).startActivityForResult(intent, ACTIVITY_REQUEST_CHATSCREEN);
            }
        });
        return convertView;
    }

    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        usersList.clear();
        if (searchText.length() == 0) {
            usersList.addAll(copyOfUsersList);
        } else {
            for (UserPOJO user : copyOfUsersList) {
                if (user.getContactname_inphone().toLowerCase(Locale.getDefault()).contains(searchText)
                        || user.getPhonenumber().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    usersList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
