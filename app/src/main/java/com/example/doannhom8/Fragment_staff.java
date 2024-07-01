package com.example.doannhom8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Fragment_staff extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_staff() {
    }

    public static Fragment_staff newInstance(String param1, String param2) {
        Fragment_staff fragment = new Fragment_staff();
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
    ImageView btnAddStaff;
    ArrayList<Staff> staffs;
    StaffAdapter adapter;
    ListView listView ;
    FirebaseAuth mAuth;
    DocumentReference db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff, container, false);

        listView = root.findViewById(R.id.ListOfStaffs);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        db.collection("/NHANVIEN").get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                staffs = new ArrayList<>();
                for (QueryDocumentSnapshot data : task.getResult())
                {
                    String CCCD = data.getString("CCCD"),
                            HOTEN = data.getString("HOTEN"),
                            NGAYSINH = data.getString("NGAYSINH"),
                            GIOITINH = data.getString("GIOITINH"),
                            SDT = data.getString("SDT"),
                            NGVL = data.getString("NGVL"),
                            CHUCVU = data.getString("CHUCVU"),
                            MANV = data.getId(),
                            Email = data.getString("EMAIL");

                    staffs.add(new Staff(MANV, HOTEN, NGAYSINH, GIOITINH ,SDT, CHUCVU, CCCD, Email, NGVL));
                }
                adapter = new StaffAdapter(getActivity(), R.layout.layout_staff_manage, staffs);
                listView.setAdapter(adapter);
            }
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Bundle bundle = new Bundle();
            bundle.putString("MANV", staffs.get(i).MaNhanVien());
            Navigation.findNavController(view).navigate(R.id.action_fragment_staff_to_fragment_staff_info, bundle);
        });

        root.findViewById(R.id.btnAddInfo).setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_fragment_staff_to_fragment_staff_edit);
        });

        return root;
    }
}
