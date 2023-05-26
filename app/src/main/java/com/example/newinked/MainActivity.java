package com.example.newinked;

import static com.example.newinked.R.id;
import static com.example.newinked.R.layout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button registerButton;
    TextView aboutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        registerButton = findViewById(id.registerButton);
        registerButton.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        loginButton = findViewById(id.loginButtonUsuario);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
            startActivity(intent);
        });

        aboutTextView = findViewById(id.tvAbout);
        aboutTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });


    }
}
