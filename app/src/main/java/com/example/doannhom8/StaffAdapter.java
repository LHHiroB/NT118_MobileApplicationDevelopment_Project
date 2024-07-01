package com.example.doannhom8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

class Staff {
    private String MANV, HOTEN, NGAYSINH, GIOITINH, SDT, CHUCVU, CCCD, NGVL, LUONG;
    private String email;

    public Staff(String ma_nha_vien,
                 String ho_va_ten,
                 String ngay_thang_nam_sinh,
                 String gioi_tinh,
                 String so_dien_thoai,
                 String chuc_vu,
                 String cccd_cmnd,
                 String email,
                 String ngay_vao_lam) {
        MANV = ma_nha_vien;
        HOTEN = ho_va_ten;
        NGAYSINH = ngay_thang_nam_sinh;
        GIOITINH = gioi_tinh;
        SDT = so_dien_thoai;
        CHUCVU = chuc_vu;
        CCCD = cccd_cmnd;
        this.email = email;

        NGVL = ngay_vao_lam;

        if (Objects.equals(CHUCVU, "Quản lý"))
            LUONG = "15.000.000đ";
        else if (Objects.equals(CHUCVU, "Phục vụ"))
            LUONG = "8.000.000đ";
        else if (Objects.equals(CHUCVU, "Thu ngân"))
            LUONG = "10.000.000đ";
        else if (Objects.equals(CHUCVU, "Bảo vệ"))
            LUONG = "8.000.000đ";
        else if (Objects.equals(CHUCVU, "Pha chế"))
            LUONG = "8.000.000đ";
        else
            LUONG = "5.000.000đ";
    }

    public String CCDD_CMND() {
        return CCCD;
    }

    public String MaNhanVien() {
        return MANV;
    }

    public String ChucVu() {
        return CHUCVU;
    }

    public String HoVaTen() {
        return HOTEN;
    }

    public String SoDienThoai() {
        return SDT;
    }


    public String NgayThangNamSinh() {
        return NGAYSINH;
    }

    public String GioiTinh() {
        return GIOITINH;
    }

    public String getEmail() {
        return email;
    }

    public String NgayVaoLam() {
        return NGVL;
    }
    public String Luong() { return LUONG; }
}

public class StaffAdapter extends BaseAdapter {

    private Context m_Context;
    private ArrayList<Staff> m_data;
    private int m_Layout;


    public StaffAdapter(Context context, int layout, ArrayList<Staff> data) {
        m_Context = context;
        m_data = data;
        m_Layout = layout;
    }

    @Override
    public int getCount() {
        return m_data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(m_Context).inflate(m_Layout, null);

        ((TextView)view.findViewById(R.id.tvName)).setText(m_data.get(i).HoVaTen());
        ((TextView)view.findViewById(R.id.tvPosition)).setText(m_data.get(i).ChucVu());
        ((TextView)view.findViewById(R.id.tvPhone)).setText(m_data.get(i).SoDienThoai());
        ((TextView)view.findViewById(R.id.tvGender)).setText(m_data.get(i).GioiTinh());

        ImageLoader.Load( "images/staff/" + m_data.get(i).MaNhanVien() + ".jpg", ((ImageView)view.findViewById(R.id.imgName)));

        return view;
    }
}
