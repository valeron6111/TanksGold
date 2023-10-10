package com.android.tanksgold;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EarningsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        // Найдите кнопку "Заработать на выполнении заданий" в макете fragment_earnings.xml
        Button offerwallButton = view.findViewById(R.id.offerwallButton);

        // Назначьте обработчик щелчка для кнопки
        offerwallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Действия, которые выполняются при нажатии на кнопку "Заработать на выполнении заданий"
                if (getActivity() instanceof ProfileActivity) {
                    ((ProfileActivity) getActivity()).showOfferDialog();
                }
            }
        });

        return view;
    }
}
