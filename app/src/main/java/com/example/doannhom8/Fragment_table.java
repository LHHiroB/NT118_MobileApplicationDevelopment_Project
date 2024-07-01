package com.example.doannhom8;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fragment_table extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_table() {
    }

    public static Fragment_table newInstance(String param1, String param2) {
        Fragment_table fragment = new Fragment_table();
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

    GridView tableList;
    ArrayList<MyBool> table;
    TableAdapter adapter;
    MenuBuilder menuBuilder;
    DocumentReference db;
    FirebaseAuth mAuth;

    ImageView addImg;
    SwipeRefreshLayout refreshLayout;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        tableList = view.findViewById(R.id.tableList);
        addImg = view.findViewById(R.id.btnAddTable);
        refreshLayout = view.findViewById(R.id.swipeLayoutTable);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        addImg.setOnClickListener(view12 -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_table_add);

            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            if(window == null){
                return;
            }

            dialog.findViewById(R.id.btnCancel).setOnClickListener(view1 ->  {
                dialog.dismiss();
            });

            dialog.findViewById(R.id.btnAdd).setOnClickListener(view2 -> {
                String s = ((EditText)dialog.findViewById(R.id.edtTableIndex)).getText().toString();
                if (!s.isEmpty())
                {
                    int index = Integer.parseInt(s);
                    db.collection("TableStatus").whereEqualTo("Index", index).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot data : task.getResult())
                                {
                                    CustomToast.e(getActivity(), "Bàn số " + index + " đã có sẵn", Toast.LENGTH_SHORT);
                                    return;
                                }
                                Map<String, Object> map = new HashMap<>();
                                map.put("Index", index);
                                db.collection("TableStatus").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("status", false);
                                        db.collection("/TableStatus/").document(task.getResult().getId()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                CreateList();
                                            }
                                        });
                                    }
                                });
                                dialog.dismiss();
                            }
                            else
                                CustomToast.e(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG);
                        }
                    });
                }
                else
                    CustomToast.e(getActivity(), "Vui lòng nhập số bàn!", Toast.LENGTH_SHORT);
            });

            dialog.show();

            CreateList();
        });


        tableList.setOnItemClickListener((adapterView, view13, i, l) -> {
            if(refreshLayout.isRefreshing()) return;

            menuBuilder = new MenuBuilder(getContext());
            MenuInflater inflater1 = new MenuInflater(getContext());

            if(table.get(i).Get()){
                inflater1.inflate(R.menu.menu_occupied, menuBuilder);
            }
            else{
                inflater1.inflate(R.menu.menu_empty, menuBuilder);
            }

            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getContext(), menuBuilder, view13);
            menuPopupHelper.setForceShowIcon(true);

            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.tinhTien:
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("key1", table.get(i).getId());
                            Navigation.findNavController(view13).navigate(R.id.action_fragment_table_to_fragment_bill, bundle);
                            break;
                        }
                        case R.id.goiMon:
                        {
                            Bundle bund = new Bundle();
                            bund.putString("soban", table.get(i).getId());
                            Navigation.findNavController(view13).navigate(R.id.action_fragment_table_to_fragment_menu,bund);
                            break;
                        }
                        case R.id.xoaBan:
                        {
                            final Dialog dialog = new Dialog(getContext());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.layout_payment);

                            ((TextView)dialog.findViewById(R.id.textView2)).setText("Bạn có muốn xóa bàn số " + table.get(i).getIndex());
                            ((Button)dialog.findViewById(R.id.btnNo)).setText("Không");
                            ((Button)dialog.findViewById(R.id.btnThanhToan)).setText("Có");
                            Window window = dialog.getWindow();
                            if(window == null){
                                return true;
                            }

                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            WindowManager.LayoutParams windowAttribute = window.getAttributes();
                            windowAttribute.gravity = Gravity.CENTER;
                            window.setAttributes(windowAttribute);

                            dialog.findViewById(R.id.btnNo).setOnClickListener(view131 -> dialog.dismiss());

                            dialog.findViewById(R.id.btnThanhToan).setOnClickListener(view1312 -> {
                                db.collection("TableStatus").document(table.get(i).getId()).delete().addOnCompleteListener(task -> {
                                    CustomToast.w(getActivity(), "Đã xóa bàn số " + table.get(i).getIndex() + "!", Toast.LENGTH_LONG);
                                    CreateList();
                                });
                                dialog.dismiss();
                            });
                            dialog.show();
                            break;
                        }
                    }
                    return true;
                }

                @Override
                public void onMenuModeChange(@NonNull MenuBuilder menu) {
                }
            });

            menuPopupHelper.show();
        });
        refreshLayout.setOnRefreshListener(() -> {
            CreateList();
            refreshLayout.setRefreshing(false);
        });

        CreateList();

        return view;
    }

    private void CreateList() {

        db.collection("/TableStatus").orderBy("Index", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    table = new ArrayList<>();
                    adapter = new TableAdapter(getContext(), table);
                    tableList.setAdapter(adapter);
                    for (QueryDocumentSnapshot data : task.getResult()) {
                        MyBool val = new MyBool(data.getId(), false, Integer.parseInt(data.getLong("Index")+""));
                        table.add(val);

                        db.collection("TableStatus/" + data.getId() + "/DrinksOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                for (DocumentSnapshot data : task1.getResult())
                                {
                                    val.Set(true);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }

                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}