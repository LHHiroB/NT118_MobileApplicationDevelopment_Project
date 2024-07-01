package com.example.doannhom8;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Fragment_item_edit extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_item_edit() {
    }

    public static Fragment_item_edit newInstance(String param1, String param2) {
        Fragment_item_edit fragment = new Fragment_item_edit();
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

    EditText edtNameDrink, edtPrice;
    ImageView imgDrink;
    Button btnSaveDrink;
    String query = "";

    FirebaseAuth mAuth;
    DocumentReference db;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Uri data = result.getData().getData();
                        imgDrink.setImageURI(data);
                    }
                }
            });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_edit, container, false);

        edtNameDrink = v.findViewById(R.id.edtNameDrink);
        edtPrice = v.findViewById(R.id.edtPrice);
        imgDrink = v.findViewById(R.id.imgEditDink);
        btnSaveDrink = v.findViewById(R.id.btnSaveDrink);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        imgDrink.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            launcher.launch(galleryIntent);
        });

        btnSaveDrink.setOnClickListener(view -> {
            ImageLoader.Upload("images/goods/", imgDrink);
        });
        Bundle data = getArguments();

        switch (data.getString("temp"))
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
        if (data.getString("Masp") != null)
        {
            db.collection(query).document(data.getString("Masp"))
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snap = task.getResult();
                            edtNameDrink.setText(snap.getString("TEN"));
                            edtPrice.setText( ""+snap.getLong("GIA"));
                        }
                    });
            ImageLoader.Load("images/goods/" + data.getString("Masp") + ".jpg", v.findViewById(R.id.imgEditDink));
        }
        v.findViewById(R.id.btnSaveDrink).setOnClickListener(view ->{

            String tensp = edtNameDrink.getText().toString(),
                    gia = edtPrice.getText().toString();

            if (tensp.isEmpty() || gia.isEmpty())
            {
                CustomToast.e(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT);
                return;
            }

            long giasp = Integer.parseInt(gia);

            if (data.getString("Masp") == null) // add mode
            {
                Map<String, Object> drink = new HashMap<>();
                drink.put("TEN", tensp);
                drink.put("GIA", giasp);

                db.collection(query).add(drink).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful())
                        {
                            ImageLoader.Upload("images/goods/" + task.getResult().getId() + ".jpg", imgDrink);
                        }
                    }
                });
            }
            else
            {
                Map<String, Object> drink = new HashMap<>();
                drink.put("TEN", tensp);
                drink.put("GIA", giasp);
                String id = getArguments().getString("Masp");
                db.collection(query).document(id).set(drink);
                ImageLoader.Upload("images/goods/" + id + ".jpg", imgDrink);
            }

            CustomToast.i(getContext(), "Lưu thành công", Toast.LENGTH_SHORT);

            getActivity().onBackPressed();
        });
        v.findViewById(R.id.btnBack2).setOnClickListener(view -> getActivity().onBackPressed());
        return v;
    }
}