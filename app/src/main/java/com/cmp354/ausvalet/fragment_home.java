package com.cmp354.ausvalet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home newInstance(String param1, String param2) {
        fragment_home fragment = new fragment_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<String> names =  new ArrayList<>();
    ArrayList<String> ids =  new ArrayList<>();
    ArrayList<String> points =  new ArrayList<>();

    FirebaseFirestore db;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = getView().findViewById(R.id.listview);
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("available", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user =   document.toObject(User.class);
                                Log.d("Test", "Beginning of Test");
                                Log.d("Test", "This is: " + user.getFirst() + " " + user.getLast());
                                Log.d("Test", user.getId());
                                Log.d("Test", user.getPoints() + "");
                                names.add(user.getFirst() + " " + user.getLast());
                                Log.d("Test", names.get(0));
                                ids.add(user.getId());
                                points.add(user.getPoints() + "");


                            }
                            //TODO: Fix applicaiton context returning null when spam clicking home fragment button
                            CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(
                                    getActivity().getApplicationContext(), names, ids, points);

                            listView.setAdapter(customBaseAdapter);
                        } else {
                            Log.d("HOME", "Error getting documents: ", task.getException());
                        }
                    }
                });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: Transfer to detailed activity

                Intent intent = new Intent(getActivity().getApplicationContext(), BookCaptain.class);
                intent.putExtra("captainId", ids.get(i));
                intent.putExtra("customerId", getArguments().getString("id"));
                startActivity(intent);
            }
        });
    }



}