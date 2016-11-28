package asee.giiis.unex.es.mysporttraining.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.FriendsAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.User;
import asee.giiis.unex.es.mysporttraining.R;

public class FriendsFragment extends Fragment {

     RecyclerView mRecyclerView;
     RecyclerView.Adapter mAdapter;
     List<User> mUserList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        prepareData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friend_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Adapter
        mAdapter = new FriendsAdapter(this.getActivity(), mUserList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void prepareData(){
//        User user = new User("josecarloses", "Jose", "Escudero", "55502");
//        mUserList.add(user);
//
//        user = new User("jimjujim", "Judit", "Jimenez", "2302");
//        mUserList.add(user);
//
//        user = new User("javitan", "Javier", "Garcia", "85620");
//        mUserList.add(user);
//
//        user = new User("azaharact", "Azahara", "Campos", "28701");
//        mUserList.add(user);
//
//        user = new User("visen", "Vicente", "Gonzalez", "7620");
//        mUserList.add(user);
//        user = new User("jimjujim", "Judit", "Jimenez", "2302");
//        mUserList.add(user);
//
//        user = new User("javitan", "Javier", "Garcia", "875620");
//        mUserList.add(user);
    }

}
