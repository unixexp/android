package com.example.funnychat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.funnychat.MainActivity;
import com.example.funnychat.R;
import com.example.funnychat.models.Message;
import com.example.funnychat.models.User;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private final LayoutInflater layoutInflater;

    public MessagesAdapter(@NonNull Context context, @NonNull List<Message> objects) {
        super (context, -1, objects);
        layoutInflater = LayoutInflater.from (context);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.message_list_item,
                    parent, false);
        }

        final Message message = getItem (position);

        final List<User> usersAll = ((MainActivity) getContext()).getUsersAll();
        User ownUser = null;
        for (User user : usersAll) {
            if (user.getId() == message.getOwnUserId()) {
                ownUser = user;
                break;
            }
        }

        final TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        if (ownUser != null)
            userName.setText (ownUser.getName() + ":");

        final TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText (message.getText());

        return convertView;
    }

}
