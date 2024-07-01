package com.example.doannhom8;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_order extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_order() {
    }

    public static Fragment_order newInstance(String param1, String param2) {
        Fragment_order fragment = new Fragment_order();
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
    TextView name,soluong,gia,l,m;
    Button btnthemngay;
    Boolean bl,bm;
    ImageView add,remove;
    int sl;
    Bundle bund;
    String size = "M";
    String masp, theloai, tableId;
    MaterialCardView selectCard;
    TextView tvtopping;
    ImageView image;
    boolean[] selecttopping;
    ArrayList<Integer> toppinglist;

    FirebaseAuth mAuth;
    DocumentReference db;

    ArrayList<Product> arraytopping;

    int tientopping = 0 ;

    String[] mangtengiatopping,mangtentopping;
    int[] manggiatopping;
    String tentoppingdachon = "";
    final Calendar instance = Calendar.getInstance();
    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance().document("CUAHANG/" + mAuth.getUid());

        arraytopping = new ArrayList<>();

        name = v.findViewById(R.id.tvOrder);
        soluong = v.findViewById(R.id.tvQuantity);

        gia = v.findViewById(R.id.tvPrice);

        m = v.findViewById(R.id.sizeM);
        l = v.findViewById(R.id.sizeL);

        image = v.findViewById(R.id.imgOder);

        bund = getArguments();

        tableId = bund.getString("soban");
        masp = bund.getString("MASP");
        theloai = bund.getString("theloai");

        ImageLoader.Load("images/goods/" + masp + ".jpg", image);

        add = v.findViewById(R.id.btnAdd);
        remove = v.findViewById(R.id.btnRemove);
        sl = 1;
        bm = true;
        bl = false;

        add.setOnClickListener(view -> {
            sl++;
            soluong.setText(String.valueOf(sl));
            if (bm)
                gia.setText(String.valueOf(sl*( bund.getInt("GIA") + tientopping  ) ));
            else
                gia.setText(String.valueOf(sl*( bund.getInt("GIA") + tientopping + 5000 ) ));
        });

        remove.setOnClickListener(view -> {
            if (sl>1)
            {
                sl--;
                soluong.setText(String.valueOf(sl));

                if (bm)
                    gia.setText(String.valueOf(sl*( bund.getInt("GIA") + tientopping  ) ));
                else
                    gia.setText(String.valueOf(sl*( bund.getInt("GIA") + tientopping + 5000 ) ));
            }
        });

        m.setOnClickListener(view -> {
            if ( bl )
            {
                size = "M";
                gia.setText(String.valueOf(sl*( bund.getInt("GIA")  + tientopping ) ));
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
                gia.setText(String.valueOf(sl*( bund.getInt("GIA") + 5000 + tientopping ) ));
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

        btnthemngay = v.findViewById(R.id.btnOrderNow);

        selectCard = v.findViewById(R.id.selectCard);
        tvtopping = v.findViewById(R.id.tvtopping);
        toppinglist = new ArrayList<>(); // lưu vị trí của từng phần tử

        db.collection("/SANPHAM/TOPPING/DANHSACHTOPPING").get().addOnCompleteListener(task -> {
            for(DocumentSnapshot data: task.getResult()){
                String MASP = data.getId();
                String TENSP = data.getString("TEN");
                long GIA = data.getLong("GIA");

                arraytopping.add(new Product(MASP, TENSP, Integer.parseInt(GIA + "")));
            }

            mangtentopping = new String[arraytopping.size()];
            manggiatopping = new int[arraytopping.size()];
            mangtengiatopping = new String[arraytopping.size()];

            for (int z=0;z<arraytopping.size();z++) {
                manggiatopping[z] = arraytopping.get(z).getGia();
                mangtentopping[z] = arraytopping.get(z).getTensp();
                mangtengiatopping[z] = arraytopping.get(z).getTensp() + "\t \t \t" + manggiatopping[z];
            }
            selecttopping = new boolean[mangtengiatopping.length];

            selectCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showtoppingDialog();
                }
                private void showtoppingDialog() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Lựa Chọn Topping");
                    builder.setCancelable(false);

                    builder.setMultiChoiceItems(mangtengiatopping, selecttopping, (dialogInterface, i, b) -> {
                        if (b) // khi click vào thì sẽ được thêm phần tử thứ i vào list
                            //Khi lựa chọn trong chatbox --> thêm vào trong list
                            toppinglist.add(i);
                        else
                            //Khi không chọn trong chatbox --> xóa vào trong list
                            toppinglist.remove(Integer.valueOf(i));
                    }).setPositiveButton("Chọn", (dialogInterface, i) -> {

                        StringBuilder stringBuilder = new StringBuilder();
                        tientopping = 0 ;
                        tentoppingdachon = "";
                        for ( int j=0; j < toppinglist.size();j++) // toppinglist(int): thứ tự các món đã chọn
                        {
                            // stringBuilder: 1 cái mảng lấy thành phần trong box
                            stringBuilder.append(mangtentopping[toppinglist.get(j)]); // mangtengiatopping[5]
                            tentoppingdachon +=  mangtentopping[toppinglist.get(j)];
                            tientopping += manggiatopping[toppinglist.get(j)]; // tổng tiền topping đã thanh toán
                            if (j != toppinglist.size() -1 )
                            {
                                // để kiểm tra giá trị j và thêm vào dấu ","
                                stringBuilder.append(", ");
                                tentoppingdachon += ", ";
                            }
                            tvtopping.setText(stringBuilder.toString());
                        }
                        if (bm)
                            gia.setText( String.valueOf( sl * (bund.getInt("GIA") + tientopping) ) );
                        else
                            gia.setText(String.valueOf(sl*( bund.getInt("GIA") + tientopping + 5000 ) ));
                    }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setNeutralButton("Xóa tất cả", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for ( int j = 0 ; j < selecttopping.length ; j++) // đã có cái nào chọn rồi thì false nó lại
                            {
                                selecttopping[j] = false;
                                toppinglist.clear();
                                tvtopping.setText("");
                                tentoppingdachon = "";
                                tientopping = 0 ;
                                if (bm)
                                    gia.setText( String.valueOf( sl * bund.getInt("GIA") ) );
                                else
                                    gia.setText(String.valueOf(sl*( bund.getInt("GIA")  + 5000 ) ));
                            }
                        }
                    });
                    builder.show();
                }
            });

            // tên(size), số lượng ,topping, số bàn
            btnthemngay.setOnClickListener(view -> {
                //lưu đồ order vào file
                saveFoodOrderIntoAFile();
                getActivity().onBackPressed();
            });
        });

        v.findViewById(R.id.back).setOnClickListener(view -> getActivity().onBackPressed());

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
                    String path = task.getResult().getPath();

                    for(int i = 0; i < toppinglist.size(); i++){
                        Map<String, Object> topping = new HashMap<>();
                        //String link = arraytopping.get(toppinglist.get(i)).getMasp();
                        topping.put("topping_ref", db.collection("SANPHAM/TOPPING/DANHSACHTOPPING/").document(arraytopping.get(toppinglist.get(i)).getMasp()));
                        //db.collection(  + "/Topping").add(topping);
                        task.getResult().getParent().document(task.getResult().getId()).collection("Topping").add(topping);
                    }

                    Map<String, Object> queue = new HashMap<>();
                    queue.put("food_name", db.collection("/TableStatus/" + tableId + "/DrinksOrder/").document(task.getResult().getId()));
                    queue.put("TIME", instance.getTimeInMillis() / 1000);
                    db.collection("/FoodQueue").add(queue);
                });
    }
}