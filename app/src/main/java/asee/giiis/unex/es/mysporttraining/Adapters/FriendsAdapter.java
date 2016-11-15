package asee.giiis.unex.es.mysporttraining.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Objects.User;
import asee.giiis.unex.es.mysporttraining.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    Context c;
    List<User> friendList = new ArrayList<User>();


    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsAdapter(Context c, List<User> friendList){
        this.friendList = friendList;
        this.c = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         holder.bind(friendList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
         TextView username;
         TextView firstName;
         TextView lastName;
         TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            // Get the references to every widget of the User View
            username = (TextView) itemView.findViewById(R.id.rv_friends_usr_username);
            firstName = (TextView) itemView.findViewById(R.id.rv_friends_usr_first_name);
            lastName = (TextView) itemView.findViewById(R.id.rv_friends_usr_last_name);
            score = (TextView) itemView.findViewById(R.id.rv_friends_usr_score);
        }

        public void bind (User user){
            // Display each item in layout
            username.setText(user.getUsername());
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            score.setText(user.getScore());
        }
    }

}
