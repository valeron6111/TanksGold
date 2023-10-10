package com.android.tanksgold;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.GetTokenResult;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;
    private FirebaseAuth mAuth;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        ImageView backButton = findViewById(R.id.backButton);
        errorTextView = findViewById(R.id.errorTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Закрыть текущую активность при нажатии кнопки "Назад"
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Успешная авторизация
                            mAuth.getCurrentUser().getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String authToken = task.getResult().getToken();
                                                Log.d("LoginActivity", "Полученный токен: " + authToken);

                                                AuthorizationManager.saveAuthorizationToken(authToken);
                                                // Проверяем, сохранен ли токен
                                                String savedToken = AuthorizationManager.getAuthorizationToken();
                                                if (savedToken != null) {
                                                    // Токен успешно сохранен, вы можете его вывести для проверки
                                                    Log.d("LoginActivity", "Сохраненный токен: " + savedToken);
                                                }

                                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                                finish(); // Закрыть LoginActivity
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
                                showError("Ошибка входа");
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
