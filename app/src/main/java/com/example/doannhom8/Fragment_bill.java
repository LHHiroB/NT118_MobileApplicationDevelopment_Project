package com.example.doannhom8;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_bill extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_bill() {
    }

    public static Fragment_bill newInstance(String param1, String param2) {
        Fragment_bill fragment = new Fragment_bill();
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

    DocumentReference db;
    ListView listFood;
    Bill_adapter adapter;
    ArrayList<OrderDrinks> arrayList;
    TextView overal;
    String tableId;
    ArrayList<String> ref_topping;
    FirebaseAuth mAuth;

    final Calendar instance = Calendar.getInstance();
    int sum = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());
        tableId = getArguments().getString("key1");

        listFood = view.findViewById(R.id.food_item);
        overal = view.findViewById(R.id.tien);

        overal.setText("0đ");
        getDateTime(view);
        getTableNumber(view);
        loadFoodIntoBill();

        view.findViewById(R.id.btnBack).setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_fragment_bill_to_fragment_table));

        view.findViewById(R.id.btnPay).setOnClickListener(view12 -> openPayDialog(Gravity.CENTER, view12));

        return view;
    }

    private void loadFoodIntoBill() {

        db.collection("/TableStatus/" + tableId + "/DrinksOrder").get()
                .addOnCompleteListener(task1 -> {
                    arrayList = new ArrayList<>();
                    ref_topping = new ArrayList<>();
                    adapter = new Bill_adapter(getActivity(),R.layout.layout_bill,arrayList);
                    listFood.setAdapter(adapter);

                    for(DocumentSnapshot data : task1.getResult()){
                        long GIA;

                        if(data.getBoolean("DONE")){
                            OrderDrinks good = new OrderDrinks(data.getId());
                            arrayList.add(good);
                            adapter.notifyDataSetChanged();

                            good.setSize(data.getString("SIZE"));
                            good.setSoluong(Integer.parseInt(data.getLong("SOLUONG") + ""));
                            GIA = data.getLong("GIA");
                            good.setGia(GIA);
                            sum += GIA;
                            adapter.notifyDataSetChanged();

                            data.getDocumentReference("sp_ref_name").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                    good.setName(task2.getResult().getString("TEN"));
                                    adapter.notifyDataSetChanged();

                                    if (task2.getResult().getReference().getParent().getParent().getId().equals("TRASUA")) {
                                        good.setTopping(new ArrayList<>());

                                        data.getReference().collection("Topping").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task3) {
                                                for(DocumentSnapshot dataaaa : task3.getResult()){
                                                    Product topping = new Product();
                                                    good.addTopping(topping);
                                                    adapter.notifyDataSetChanged();

                                                    dataaaa.getDocumentReference("topping_ref").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
                                                            topping.setMasp(task5.getResult().getReference().getId());
                                                            topping.setTensp(task5.getResult().getString("TEN"));
                                                            Log.i("Id", task5.getResult().getReference().getId() + "/nTen" + task5.getResult().getString("TEN"));
                                                            topping.setGia(Integer.parseInt(task5.getResult().getLong("GIA") + ""));

                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                            overal.setText(sum + " đ");
                        }
                    }
                    adapter.notifyDataSetChanged();
                });


    }

    private void openPayDialog(int gravity, View view) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_payment);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(Gravity.CENTER == gravity);

        dialog.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btnThanhToan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("NGHD", instance.getTimeInMillis() / 1000);
                map.put("TRIGIA", sum);

                db.collection("/HOADON/").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        db.collection("TableStatus/" + tableId + "/DrinksOrder").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        for (DocumentSnapshot data : task1.getResult()){
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("MASP", data.getDocumentReference("sp_ref_name"));
                                            map.put("SL", data.getLong("SOLUONG"));
                                            map.put("GIA", data.getLong("GIA"));
                                            map.put("SIZE", data.getString("SIZE"));

                                            task.getResult().getParent().document(task.getResult().getId()).collection("/CTHD").add(map)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task3) {
                                                            data.getReference().collection("Topping").get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                            for(DocumentSnapshot dataa : task2.getResult()){
                                                                                Map<String, Object> map = new HashMap<>();
                                                                                map.put("MATOPPING", dataa.getDocumentReference("topping_ref"));

                                                                                task3.getResult().getParent().document(task3.getResult().getId()).collection("/TOPPING").add(map)
                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                                dataa.getReference().delete();
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                            data.getReference().delete();
                                        }
                                    }
                                });
                    }
                });
                Navigation.findNavController(getParentFragment().getView()).navigate(R.id.action_fragment_bill_to_fragment_table);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getTableNumber(View view) {
        db.collection("TableStatus/").document(tableId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ((TextView)view.findViewById(R.id.table_number)).setText("Bàn " + task.getResult().getLong("Index"));
            }
        });

    }

    private void getDateTime(View view) {
        String dateTime;
        Calendar calendar;
        SimpleDateFormat simpleDateFormat;

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm EEEE, dd LLLL yyyy");
        dateTime = simpleDateFormat.format(calendar.getTime());
        ((TextView) view.findViewById(R.id.timeDate)).setText(dateTime);
    }
}