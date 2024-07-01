package com.example.doannhom8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class Fragment_item_info extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_item_info() {
    }

    public static Fragment_item_info newInstance(String param1, String param2) {
        Fragment_item_info fragment = new Fragment_item_info();
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
    String query = "";
    FirebaseAuth mAuth;
    DocumentReference db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_info, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        Bundle DrinkInfo = getArguments();

        switch (DrinkInfo.getString("temp"))
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
        db.collection(query).document(DrinkInfo.getString("Masp"))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot snap =  task.getResult();

                        ((TextView)v.findViewById(R.id.tvNameDrinks)).setText(snap.getString("TEN"));
                        ((TextView)v.findViewById(R.id.tvGia)).setText(snap.getLong("GIA")+" đ");
                    }
                });

        v.findViewById(R.id.btnDelDrink).setOnClickListener(view -> {
            db.collection(query).document(DrinkInfo.getString("Masp")).delete();
            FirebaseStorage.getInstance().getReference().child("images/goods/"+ DrinkInfo.getString("Masp" + ".jpg")).delete();
            CustomToast.i(getContext(), "Xóa thành công", Toast.LENGTH_SHORT);
            getActivity().onBackPressed();
        });

        ImageLoader.Load( "images/goods/" + DrinkInfo.getString("Masp") + ".jpg", v.findViewById(R.id.avtDrink));

        v.findViewById(R.id.btnEditDrink).setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_fragment_item_info_to_fragment_item_edit, DrinkInfo);
        });
        return v;
    }
}