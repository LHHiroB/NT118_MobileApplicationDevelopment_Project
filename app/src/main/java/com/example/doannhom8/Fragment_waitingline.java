package com.example.doannhom8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_waitingline extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_waitingline() {
    }

    public static Fragment_waitingline newInstance(String param1, String param2) {
        Fragment_waitingline fragment = new Fragment_waitingline();
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ListView listDrinks;
    SwipeRefreshLayout refreshLayout;
    OrderDrinksAdapter adapter;
    ArrayList<OrderDrinks> arrayList;
    DocumentReference db;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waitingline, container, false);
        listDrinks = view.findViewById(R.id.lvDrinkStack);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        refreshLayout = view.findViewById(R.id.swipeLayoutHome);
        refreshLayout.setOnRefreshListener(() -> {
            LoadQueue();
            refreshLayout.setRefreshing(false);
        });
        LoadQueue();
        return view;
    }
    private void LoadQueue() {
        db.collection("/FoodQueue/").orderBy("TIME", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                arrayList = new ArrayList<>();
                adapter = new OrderDrinksAdapter(getActivity(),R.layout.layout_menu_item,arrayList);
                listDrinks.setAdapter(adapter);

                for (DocumentSnapshot data : task1.getResult()) {
                    OrderDrinks good = new OrderDrinks(data.getId());
                    arrayList.add(good);
                    adapter.notifyDataSetChanged();

                    data.getDocumentReference("food_name").get().addOnCompleteListener(task2 -> {
                        task2.getResult().getReference().getParent().getParent().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
                                good.setSoban(Integer.parseInt(task5.getResult().getLong("Index")+""));
                                adapter.notifyDataSetChanged();
                            }
                        });
                        good.setSize(task2.getResult().getString("SIZE"));
                        good.setGia(task2.getResult().getLong("GIA"));
                        good.setSoluong(Integer.parseInt(task2.getResult().getLong("SOLUONG")+""));
                        adapter.notifyDataSetChanged();

                        task2.getResult().getDocumentReference("sp_ref_name").get().addOnCompleteListener(task3 -> {
                            good.setName(task3.getResult().getString("TEN"));
                            adapter.notifyDataSetChanged();

                            if (task3.getResult().getReference().getParent().getParent().getId().equals("TRASUA")) {
                                good.setTopping(new ArrayList<Product>());
                                task2.getResult().getReference().collection("Topping").get().addOnCompleteListener(task4 -> {
                                    for(DocumentSnapshot dataaaa : task4.getResult()){
                                        Product topping = new Product();
                                        good.addTopping(topping);
                                        adapter.notifyDataSetChanged();
                                        dataaaa.getDocumentReference("topping_ref").get().addOnCompleteListener(task5 -> {
                                            topping.setMasp(task5.getResult().getReference().getId());
                                            topping.setTensp(task5.getResult().getString("TEN"));
                                            topping.setGia(Integer.parseInt(task5.getResult().getLong("GIA") + ""));
                                            adapter.notifyDataSetChanged();
                                        });
                                    }
                                });
                            }
                        });
                    });
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}