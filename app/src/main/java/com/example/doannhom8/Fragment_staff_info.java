package com.example.doannhom8;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class Fragment_staff_info extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_staff_info() {
    }

    public static Fragment_staff_info newInstance(String param1, String param2) {
        Fragment_staff_info fragment = new Fragment_staff_info();
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
    FirebaseAuth mAuth;
    DocumentReference db;
    String Phone;
    String Email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff_info, container, false);

        Bundle staffInfo = getArguments();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        db.collection("/NHANVIEN/").document(staffInfo.getString("MANV"))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot snap =  task.getResult();
                        ((TextView)root.findViewById(R.id.edtName)).setText(snap.getString("HOTEN"));
                        ((TextView)root.findViewById(R.id.tvPosition)).setText(snap.getString("CHUCVU"));
                        ((TextView)root.findViewById(R.id.tvGender)).setText(snap.getString("GIOITINH"));
                        ((TextView)root.findViewById(R.id.tvCCCD)).setText(snap.getString("CCCD"));
                        ((TextView)root.findViewById(R.id.tvDob)).setText(snap.getString("NGAYSINH"));
                        ((TextView)root.findViewById(R.id.tvPhone1)).setText(snap.getString("SDT"));
                        ((TextView)root.findViewById(R.id.tvHSL)).setText(snap.getString("EMAIL"));
                        ((TextView)root.findViewById(R.id.tvBeginDate)).setText(snap.getString("NGVL"));

                        String Chucvu = snap.getString("CHUCVU");
                        String Luong = "";

                        if (Objects.equals(Chucvu, "Quản lý"))
                            Luong = "15.000.000đ";
                        else if (Objects.equals(Chucvu, "Phục vụ"))
                            Luong = "8.000.000đ";
                        else if (Objects.equals(Chucvu, "Thu ngân"))
                            Luong = "10.000.000đ";
                        else if (Objects.equals(Chucvu, "Bảo vệ"))
                            Luong = "8.000.000đ";
                        else if (Objects.equals(Chucvu, "Pha chế"))
                            Luong = "8.000.000đ";
                        else
                            Luong = "5.000.000đ";


                        ((TextView)root.findViewById(R.id.tvwages)).setText(Luong);
                        Phone = snap.getString("SDT");
                        Email = snap.getString("EMAIL");
                    }
                });

        root.findViewById(R.id.btnDelete).setOnClickListener(view -> {
            db.collection("/NHANVIEN/").document(staffInfo.getString("MANV")).delete();
            FirebaseStorage.getInstance().getReference().child("images/staff/"+ staffInfo.getString("MANV" + ".jpg")).delete();
            Navigation.findNavController(view).navigate(R.id.action_fragment_staff_info_to_fragment_staff);
        });
        root.findViewById(R.id.imgPhone).setOnClickListener(view -> {
            Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Phone));
            startActivity(intent_call);
        });
        root.findViewById(R.id.imgMessage).setOnClickListener(view -> {
            Intent intent_message = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+Phone));
            startActivity(intent_message);
        });
        root.findViewById(R.id.imgEmail).setOnClickListener(view -> {
            Intent intent_mail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+Email));
            startActivity(intent_mail);
        });

        ImageLoader.Load( "images/staff/" + staffInfo.getString("MANV") + ".jpg", ((ImageView)root.findViewById(R.id.avatar)));

        root.findViewById(R.id.btnEdit).setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_fragment_staff_info_to_fragment_staff_edit, staffInfo);
        });

        return root;
    }
}