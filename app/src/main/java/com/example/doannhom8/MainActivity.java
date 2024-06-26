package com.example.doannhom8;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView btnSignUp, btnForgotPass;
    Button btnSignIn;
    ImageView hide, show;
    EditText edtMail, edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnInput);
        hide = findViewById(R.id.hidepw);
        show = findViewById(R.id.showpw);
        edtMail = findViewById(R.id.inputUsername);
        edtPass = findViewById(R.id.inputPassword);
        btnForgotPass = findViewById(R.id.forgotPassword);
        //Chuyển đến đăng kí
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity_SignUp.class);
            startActivity(intent);
        });
        //Hiện mật khẩu
        show.setOnClickListener(view -> {
            edtPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            show.setVisibility(view.INVISIBLE);
            hide.setVisibility(view.VISIBLE);
        });
        //Ẩn mật khẩu
        hide.setOnClickListener(view -> {
            edtPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hide.setVisibility(view.INVISIBLE);
            show.setVisibility(view.VISIBLE);
        });
        //Kiểm tra user đã đăng nhập từ trước chưa
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent a = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(a);
            finish();
        }
        //Đăng nhập
        btnSignIn.setOnClickListener(view -> onClickSignUp());
        //Chuyển đến quên mật khẩu
        btnForgotPass.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity_ForgotPassword.class);
            startActivity(intent);
        });
    }
    //Hàm đăng nhập
    private void onClickSignUp() {
        String email = edtMail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        if(email.isEmpty() || email == null || password.isEmpty() || password == null)
        {
            CustomToast.e(MainActivity.this, "Vui lòng điền đầy đủ thông tin đăng nhập",
                    Toast.LENGTH_SHORT);
        }
        else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent= new Intent(MainActivity.this, MainActivity2.class);
                            startActivity(intent);
                        } else {
                            //Test customToast
                            CustomToast.e(MainActivity.this, "Vui lòng kiểm tra lại thông tin.",
                                    Toast.LENGTH_SHORT);
                        }
                    });
        }
    }
}
