package com.example.doannhom8;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity_ForgotPassword extends AppCompatActivity {
    ImageView btnBack;
    TextView btnSignUp;
    Button btnSend;
    EditText Email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_forgot_password);
        btnBack = findViewById(R.id.btnBacked);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSend = findViewById(R.id.btnSend);
        Email = findViewById(R.id.inputUsername);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity_ForgotPassword.this, MainActivity.class);
            startActivity(intent);
        });
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity_ForgotPassword.this, MainActivity_SignUp.class);
            startActivity(intent);
        });
        btnSend.setOnClickListener(view -> onClickForgotPassword());

    }
    private void onClickForgotPassword() {

        String email= Email.getText().toString().trim();
        if(email.isEmpty() || email==null)
        {
            Toast.makeText(MainActivity_ForgotPassword.this,"Vui lòng nhập email", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity_ForgotPassword.this, "Đã gửi đến Email", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(MainActivity_ForgotPassword.this,"Vui lòng kiểm tra lại email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
