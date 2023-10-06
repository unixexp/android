package com.example.funnychat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.funnychat.R;
import com.example.funnychat.models.User;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

    private final LayoutInflater layoutInflater;

    public UsersAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, -1, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.user_list_item, parent,
                    false);
        }

        // Получаем доступ к нужной модели из массива по идентификатору
        final User user = getItem (position);

        // Создаем ссылки для управления виджетами макета из Java-кода
        final TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        final ImageView userImage = (ImageView) convertView.findViewById(R.id.user_state_image);

        // Теперь опишем логику визуализации элемента, основанную на нескольких параметрах
        // Итак, начнем с условия когда пользователь находится в OnLine режиме
        if (user.isLoggedIn()) {
            // Далее нам необходимо подключить нужный Drawable взависимости
            // от пренадлежности пользователя к определенному полу
            if (user.getSex().equals("male")) {
                userImage.setVisibility(View.VISIBLE);
                userImage.setBackgroundResource (R.drawable.ic_user_male_online);
            } else if (user.getSex().equals("female")) {
                userImage.setVisibility(View.VISIBLE);
                userImage.setBackgroundResource (R.drawable.ic_user_female_online);
            } else {
                // Если пол не указан - скрываем виджет ImageView
                userImage.setVisibility(View.INVISIBLE);
                // Несколько слов о возможных значениях свойства "visibility"
                // Всего существует три значения:
                // VISIBLE - Обьект является видимым на экране
                // INVISIBLE - обьект является невидимым, однако занимает место на экране
                //             Это так же значит что при позиционировании и масштабировании
                //             остальных виджетов положение и размеры данного виджета так же
                //             будут учтены
                //
                // GONE - обьект является невидимым и не занимает место на экране
            }

            // Затем укажем цвет текста
            userName.setTextColor(getContext().getResources().getColor(R.color.colorTextField));

            // Укажем стиль шрифта
            // В данном случае принимается так же во внимание условие, является ли
            // данный пользователь выбранным для общения
            if (user.isSelected())
                userName.setTypeface(Typeface.DEFAULT_BOLD);
            else
                userName.setTypeface(Typeface.DEFAULT);
        } else {
            // По аналогии опишем логику для состояния когда пользователь
            // находится в OffLine режиме
            if (user.getSex().equals("male")) {
                userImage.setVisibility(View.VISIBLE);
                userImage.setBackgroundResource (R.drawable.ic_user_male_offline);
            } else if (user.getSex().equals("female")) {
                userImage.setVisibility(View.VISIBLE);
                userImage.setBackgroundResource (R.drawable.ic_user_female_offline);
            } else {
                userImage.setVisibility(View.INVISIBLE);
            }

            userName.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));

            if (user.isSelected())
                userName.setTypeface(Typeface.DEFAULT_BOLD);
            else
                userName.setTypeface(Typeface.DEFAULT);
        }

        // И, наконец установим имя пользователя для виджета TextView
        userName.setText(user.getName());

        return convertView;
    }

}
