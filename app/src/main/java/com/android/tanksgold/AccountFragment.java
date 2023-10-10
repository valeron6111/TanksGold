package com.android.tanksgold;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Находим кнопку выхода и устанавливаем для нее обработчик события
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия кнопки "Выход" - вызываем метод выхода из аккаунта
                handleLogout();
            }
        });

        return view;
    }

    // Метод для обработки выхода из аккаунта
    private void handleLogout() {
        // Очищаем авторизационный токен
        AuthorizationManager.removeAuthorizationToken();

        // Выходим из Firebase (если используется аутентификация Firebase)
        FirebaseAuth.getInstance().signOut();

        // Возвращаемся на главную активность (MainActivity)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Закрываем текущую активность
    }
}
