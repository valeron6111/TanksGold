package com.android.tanksgold;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private DatabaseReference databaseReference;
    private TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Находим нижнюю навигацию и текстовое поле для баланса
        bottomNavigation = findViewById(R.id.bottomNavigation);
        balanceTextView = findViewById(R.id.balanceTextView);

        // Устанавливаем слушателя для элементов нижней навигации
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        // Инициализируем Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://tanks-ead4f-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference("data");

        // Отображаем фрагмент с заработком по умолчанию
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new EarningsFragment()).commit();

        // Выводим данные из Firebase Realtime Database
        printDataFromDatabase();

        View fragmentView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_earnings, null);
        Button offerwallButton = fragmentView.findViewById(R.id.offerwallButton);
        offerwallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOfferDialog(); // Вызываем метод для отображения всплывающего окна
            }
        });
    }

    // Метод для вывода данных из Firebase Realtime Database
    private void printDataFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();

                    if (dataMap != null) {
                        if (dataMap.containsKey("sub_id") && dataMap.get("sub_id").equals(currentUid)) {
                            if (dataMap.containsKey("amount")) {
                                String balance = dataMap.get("amount").toString();
                                String balanceText = "Текущий баланс: " + balance + " золота";
                                balanceTextView.setText(balanceText);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Не удалось прочитать данные.", databaseError.toException());
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.earnings) {
                selectedFragment = new EarningsFragment();
            } else if (item.getItemId() == R.id.withdraw_gold) {
                selectedFragment = new WithdrawGoldFragment();
            } else if (item.getItemId() == R.id.account) {
                // Заменяем текущий фрагмент на AccountFragment
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountFragment()).commit();
            }

            return true;
        }
    };

    // Метод для обработки выхода из аккаунта
    private void handleLogout() {
        // Очищаем авторизационный токен
        AuthorizationManager.removeAuthorizationToken();

        // Выходим из Firebase (если используется аутентификация Firebase)
        FirebaseAuth.getInstance().signOut();

        // Переходим на главную активность (MainActivity) и закрываем текущую активность (ProfileActivity)
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Закрываем текущую активность
    }

    // Метод для отображения всплывающего окна с предложениями
    void showOfferDialog() {
        // Создаем всплывающее окно (диалог)
        Dialog offerDialog = new Dialog(ProfileActivity.this);
        offerDialog.setContentView(R.layout.popup_offer_layout);

        // Здесь вы можете настроить содержимое всплывающего окна (диалога) с предложениями
        // Например, установите текст, изображения и другие элементы в соответствии с данными из JSON.

        // Покажем всплывающее окно
        offerDialog.show();
    }
}
