package com.cmp354.ausvalet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_captain_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_captain_home extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static FirebaseFirestore db;
    static String id;
    String first;
    String last;
    String number;
    int points;
    boolean isCaptain;
    boolean isAvailable;

    //TODO: FOR DAIM, try to get the status for dropped
    boolean isDropped = true;

    static User customer;
    Request req;

    Car car;

    static TextView tv_requestNotice;
    static TextView tv_info;
    static Button btn_accept;
    static Button btn_continue;
    static Button btn_decline;

    String parkingLocation;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_captain_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_captain_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_captain_home newInstance(String param1, String param2) {
        fragment_captain_home fragment = new fragment_captain_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        return inflater.inflate(R.layout.fragment_captain_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_requestNotice=view.findViewById(R.id.tv_requestNotice);
        tv_info=view.findViewById(R.id.tv_info);
        btn_accept=view.findViewById(R.id.btn_accept);
        btn_decline=view.findViewById(R.id.btn_decline);
        btn_continue=view.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_decline.setOnClickListener(this);
        tv_requestNotice.setText("No Current Requests :(");
        tv_info.setVisibility(View.GONE);
        btn_accept.setVisibility(View.GONE);
        btn_decline.setVisibility(View.GONE);
        btn_continue.setVisibility(View.GONE);


        id = getArguments().getString("id");
        first =getArguments().getString("first");
        last = getArguments().getString("last");
        number = getArguments().getString("number");
        points = getArguments().getInt("points");
        isAvailable = getArguments().getBoolean("isAvailable");
        isCaptain = getArguments().getBoolean("isCaptain");

        db=FirebaseFirestore.getInstance();
        //TODO when customer book make is available false
        db.collection("requests")
                .whereEqualTo("captainId", id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("daim", "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
//                            Log.d("daim",doc.toString());
                            if (doc.get("status").equals("requested")) {
                                //display and make buttons available
                                req=doc.toObject(Request.class);
                                //TODO display
                                db=FirebaseFirestore.getInstance();
                                Log.d("daimtest",req.getCustomerId());
                                db.collection("users")
                                        .whereEqualTo("id", req.getCustomerId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        customer=document.toObject(User.class);
                                                        String str=tv_info.getText().toString()+customer.toString()+req.toString();
                                                        tv_info.setText(str);
                                                    }
                                                } else {
                                                    Log.d("daimtest", "Error getting user documents: ", task.getException());
                                                }
                                            }
                                        });

                                db.collection("cars")
                                        .whereEqualTo("id", req.getCustomerId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        car=document.toObject(Car.class);
                                                        String str=tv_info.getText().toString()+car.toString();
                                                        tv_info.setText(str);
                                                    }
                                                } else {
                                                    Log.d("daimtest", "Error getting car documents: ", task.getException());
                                                    Log.d("daimtest", "Error getting car documents: ", task.getException());
                                                }
                                            }
                                        });





                                tv_requestNotice.setText("View Request");
                                tv_info.setVisibility(View.VISIBLE);
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_decline.setVisibility(View.VISIBLE);
//                                btn_continue.setVisibility(View.VISIBLE);





                            }

                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_accept){//accept
            accept();
        }else if(v.getId()==R.id.btn_decline){//decline
            decline();
        }else{//btn_continue

            //TODO FOR ABDU
            if(isDropped == true){
                Intent i = new Intent(getActivity().getApplicationContext(), InstructActivity.class);
                i.putExtra("iText" , "Drive to the " + req.getParkingLocation());
                i.putExtra("btnText", "I have parked the car");
                startActivity(i);

                //TODO: Once
            }


        }

    }

    public void accept(){
        //TODO display gps button
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","accepted")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request accepted in captain fragment");
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.VISIBLE);


                                            }
                                        });


                            }
                        }

                    }
                });
    }
    public static void decline(){


        //TODO remove view
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","declined")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request declined in captain fragment");
                                                tv_requestNotice.setText("No current Requests :(");
                                                tv_info.setVisibility(View.GONE);
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.GONE);


                                            }
                                        });


                            }
                        }

                    }
                });
    }


    public static void parked(){


        //TODO remove view
        db.collection("requests")
                .whereEqualTo("captainId", id)//TODO change it to isCaptain
                .whereEqualTo("customerId", customer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref=db.collection("requests").document(document.getId());
                                ref.update("status","parked")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //TODO implement
                                                Log.d("daim","request parked in captain fragment");
                                                tv_requestNotice.setText("No current Requests :(");
                                                tv_info.setVisibility(View.GONE);
                                                btn_accept.setVisibility(View.GONE);
                                                btn_decline.setVisibility(View.GONE);
                                                btn_continue.setVisibility(View.GONE);


                                            }
                                        });


                            }
                        }

                    }
                });
    }
}