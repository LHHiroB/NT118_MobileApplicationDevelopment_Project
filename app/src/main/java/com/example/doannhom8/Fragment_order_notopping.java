package com.example.doannhom8;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_order_notopping extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_order_notopping() {
    }

    public static Fragment_order_notopping newInstance(String param1, String param2) {
        Fragment_order_notopping fragment = new Fragment_order_notopping();
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

    TextView name,soluong,gia,m,l;
    Button btnthemngay;
    Boolean bl,bm;
    ImageView add,remove, image;
    int sl;
    Bundle bund;
    String size = "M", theloai, masp, tableId;
    final Calendar instance = Calendar.getInstance();
    FirebaseAuth mAuth;
    DocumentReference db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_notopping, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        name = v.findViewById(R.id.tvOrdernotopping);
        soluong = v.findViewById(R.id.tvQuantitynotopping);

        gia = v.findViewById(R.id.tvPricenotopping);

        m = v.findViewById(R.id.sizeMnotopping);
        l = v.findViewById(R.id.sizeLnotopping);

        bund = getArguments(); // lấy giá trị, có số bàn
        tableId = bund.getString("soban");
        masp = bund.getString("MASP");
        theloai = bund.getString("theloai");

        add = v.findViewById(R.id.btnAddnotopping);
        remove = v.findViewById(R.id.btnRemovenotopping);
        image = v.findViewById(R.id.imgOdernotopping);

        sl = 1;

        bm = true;
        bl = false;

        ImageLoader.Load("images/goods/" + masp + ".jpg", image);

        add.setOnClickListener(view -> {
            sl++;
            soluong.setText(String.valueOf(sl));
            if (bm)
                gia.setText(String.valueOf(sl*( bund.getInt("GIA")   ) ));
            else
                gia.setText(String.valueOf(sl*( bund.getInt("GIA")   + 5000 ) ));
        });
        remove.setOnClickListener(view -> {
            if (sl>1)
            {
                sl--;
                soluong.setText(String.valueOf(sl));

                if (bm)
                    gia.setText(String.valueOf(sl*( bund.getInt("GIA")   ) ));
                else
                    gia.setText(String.valueOf(sl*( bund.getInt("GIA")  + 5000 ) ));
            }
        });

//

        m.setOnClickListener(view -> {
            if ( bl )
            {
                size = "M";
                gia.setText(String.valueOf(sl*( bund.getInt("GIA")   ) ));
                bm = true;
                m.setTextColor(Color.parseColor("#ffffff"));
                m.setBackgroundResource(R.drawable.round_bg);

                bl = false;
                l.setText("L");
                l.setTextColor(Color.parseColor("#000000"));
                l.setBackgroundResource(R.drawable.round_bg_white);

            }
        });

        l.setOnClickListener(view -> {
            if ( bm )
            {
                size = "L";
                gia.setText(String.valueOf(sl*( bund.getInt("GIA") + 5000  ) ));
                bl = true;
                l.setTextColor(Color.parseColor("#ffffff"));
                l.setBackgroundResource(R.drawable.round_bg);

                bm = false;
                m.setText("M");
                m.setTextColor(Color.parseColor("#111111"));
                m.setBackgroundResource(R.drawable.round_bg_white);

            }
        });

        name.setText(bund.getString("TENSP"));
        gia.setText(String.valueOf(bund.getInt("GIA")));

        btnthemngay = v.findViewById(R.id.btnOrderNownotopping);
        // tên(size), số lượng ,topping, số bàn
        btnthemngay.setOnClickListener(view -> {
            saveFoodOrderIntoAFile();

            getActivity().onBackPressed();
        });

        // Xử lý nút back
        v.findViewById(R.id.backno).setOnClickListener(view -> getActivity().onBackPressed());

        return v;
    }

    private void saveFoodOrderIntoAFile() {
        Map<String, Object> map = new HashMap<>();
        map.put("sp_ref_name", db.collection(theloai).document(masp));
        map.put("SIZE", size);
        map.put("SOLUONG", Long.parseLong(soluong.getText().toString()));
        map.put("DONE", false);
        map.put("GIA", Long.parseLong(gia.getText().toString()));

        db.collection("/TableStatus/" + tableId + "/DrinksOrder").add(map)
                .addOnCompleteListener(task -> {
                    Map<String, Object> queue = new HashMap<>();
                    queue.put("food_name", db.collection("/TableStatus/" + tableId + "/DrinksOrder/").document(task.getResult().getId()));
                    queue.put("TIME", instance.getTimeInMillis() / 1000);
                    db.collection("/FoodQueue").add(queue);
                });
    }
}