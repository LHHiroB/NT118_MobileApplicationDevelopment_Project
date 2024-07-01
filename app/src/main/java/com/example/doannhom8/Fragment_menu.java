package com.example.doannhom8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class Fragment_menu extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Fragment_menu() {
    }

    public static Fragment_menu newInstance(String param1, String param2) {
        Fragment_menu fragment = new Fragment_menu();
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

    CardView coffee, trasua, sinhto, topping, topping1;
    Bundle bundletable;
    String tableId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        coffee = v.findViewById(R.id.cvCoffee);
        trasua = v.findViewById(R.id.cvTraSua);
        sinhto = v.findViewById(R.id.cvSinhTo);
        topping = v.findViewById(R.id.cvTopping);
        topping1 = v.findViewById(R.id.cvTopping1);

        if (getArguments() != null)
        {
            bundletable = getArguments();
        }

        if (bundletable!=null)
            tableId = bundletable.getString("soban");

        coffee.setOnClickListener(view -> {
            if (bundletable==null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("temp","coffee");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundle);
            }
            else
            {
                bundletable.putString("temp","coffee");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item,bundletable);
            }

        });
        trasua.setOnClickListener(view -> {

            if (bundletable == null) // đi từ home --> menu
            {
                Bundle bundle = new Bundle();
                bundle.putString("temp","trasua");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundle);
            }
            else // đi từ table --> menu
            {
                bundletable.putString("temp","trasua");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item,bundletable);
            }

        });
        sinhto.setOnClickListener(view -> {

            if (bundletable==null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("temp","sinhto");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundle);
            }
            else
            {
                bundletable.putString("temp","sinhto");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item,bundletable);
            }

        });
        topping.setOnClickListener(view -> {
            if (bundletable==null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("temp","topping");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundle);
            }
            else
            {
                bundletable.putString("temp","topping");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item,bundletable);
            }
        });
        topping1.setOnClickListener(view -> {
            if (bundletable==null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("temp","topping1");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundle);
            }

            else
            {
                bundletable.putString("temp","topping1");
                Navigation.findNavController(view).navigate(R.id.action_fragment_menu_to_fragment_menu_item2,bundletable);
            }

        });
        return v;
    }
}