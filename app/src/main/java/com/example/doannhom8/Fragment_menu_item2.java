package com.example.doannhom8;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_menu_item2 extends Fragment implements TextWatcher {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_menu_item2() {
    }

    public static Fragment_menu_item2 newInstance(String param1, String param2) {
        Fragment_menu_item2 fragment = new Fragment_menu_item2();
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

    ProductAdapterUpdate adapter;
    ArrayList<Product> arrayList;
    ListView lvcoffeeno;
    EditText edtcoffeeno;
    String tam;
    FirebaseAuth mAuth;
    DocumentReference db;

    @SuppressLint({"RestrictedApi", "Range"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_item2, container, false);

        edtcoffeeno = v.findViewById(R.id.edtcoffeeno);
        lvcoffeeno = v.findViewById(R.id.lvcoffeeno);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        Bundle bundle = getArguments();
        tam = bundle.getString("temp");
        String query = "";

        switch (tam)
        {
            case "coffee":
                query = "/SANPHAM/CAPHE/DANHSACHCAPHE";
                break;
            case "trasua":
                query = "/SANPHAM/TRASUA/DANHSACHTRASUA";
                break;
            case "sinhto":
                query = "/SANPHAM/SINHTO/DANHSACHSINHTO";
                break;
            case "topping":
                query = "/SANPHAM/TRANGMIENG/DANHSACHTRANGMIENG";
                break;
            case "topping1":
                query = "/SANPHAM/TOPPING/DANHSACHTOPPING";
                break;
        }

        edtcoffeeno.addTextChangedListener(this);
        db.collection(query).get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                arrayList = new ArrayList<>();
                adapter = new ProductAdapterUpdate(getActivity(),R.layout.layout_menu_item_notable2,arrayList);
                lvcoffeeno.setAdapter(adapter);

                for (QueryDocumentSnapshot data : task.getResult())
                {
                    String MASP = data.getId(),
                            TENSP = data.getString("TEN");
                    Long GIA = (Long)data.get("GIA");

                    arrayList.add(new Product(MASP, TENSP, GIA.intValue()));
                }
                adapter.notifyDataSetChanged();
            }
        });

        v.findViewById(R.id.btnAddDrink).setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_fragment_menu_item2_to_fragment_item_edit);
        });

        lvcoffeeno.setOnItemClickListener((adapterView, view, i, l) -> {

            bundle.putString("Masp", ((Product)adapterView.getAdapter().getItem(i)).getMasp());
            Navigation.findNavController(view).navigate(R.id.action_fragment_menu_item2_to_fragment_item_info, bundle);
        });

        v.findViewById(R.id.btnAddDrink).setOnClickListener(view -> {
            bundle.putString("Masp", null);
            Navigation.findNavController(view).navigate(R.id.action_fragment_menu_item2_to_fragment_item_edit, bundle);
        });

        // Xử lý nút back
        v.findViewById(R.id.backno).setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_fragment_menu_item2_to_fragment_menu));

        return v;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}