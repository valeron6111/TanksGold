package com.android.tanksgold;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton, backButton;
    private FirebaseAuth mAuth;
    private TextView errorTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://tanks-ead4f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("data");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        ImageView backButton = findViewById(R.id.backButton);
        errorTextView = findViewById(R.id.errorTextView);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Закрыть текущую активность при нажатии кнопки "Назад"
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            DatabaseReference userReference = databaseReference.child(uid);

                            // Записываем UID пользователя и amount=0
                            userReference.child("sub_id").setValue(uid);
                            userReference.child("amount").setValue(0);

                            mAuth.getCurrentUser().getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<com.google.firebase.auth.GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String authToken = task.getResult().getToken();

                                                // Сохраняем токен при успешной аутентификации
                                                AuthorizationManager.saveAuthorizationToken(authToken);

                                                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                                finish(); // Закрыть RegisterActivity
                                            } else {
                                                // Обработка ошибок получения токена
                                                showError("Ошибка получения токена");
                                            }
                                        }
                                    });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                // Пользователь с таким email не существует
                                showError("Пользователь с таким email не существует");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Неправильный пароль
                                showError("Неправильный пароль");
                            } catch (Exception e) {
                                // Обработка других ошибок
                                showError("Ошибка регистрации");
                            }
                        }
                    }
                });
    }

    private void showError(String errorMessage) {
        errorTextView.setText(errorMessage);
        errorTextView.setVisibility(View.VISIBLE);
    }
}
