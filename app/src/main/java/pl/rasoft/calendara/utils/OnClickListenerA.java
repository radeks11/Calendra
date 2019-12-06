package pl.rasoft.calendara.utils;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import pl.rasoft.calendara.activities.ColorPickerDialog;
import pl.rasoft.calendara.activities.ConfigActivity;

public class OnClickListenerA implements View.OnClickListener
{

    ConfigActivity _activity;
    TextView _textView;

    public OnClickListenerA(ConfigActivity activity, TextView textView) {
        _activity = activity;
        _textView = textView;
    }

    @Override
    public void onClick(View v)
    {
        int color = 0;
        if (_textView.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) _textView.getBackground();
            color = cd.getColor();
        }

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(_activity, _textView.getId(),  color);
        colorPickerDialog.setCancelable(true);
        colorPickerDialog.show();
    }

};
