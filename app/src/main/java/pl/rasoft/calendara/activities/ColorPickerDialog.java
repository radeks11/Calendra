package pl.rasoft.calendara.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.rarepebble.colorpicker.ColorPickerView;

import pl.rasoft.calendara.R;

public class ColorPickerDialog extends Dialog {

    public ConfigActivity _activity;
    protected ColorPickerView _colorPickerView;
    protected Button _colorButton;
    protected int _initialColor;
    protected int _fieldId;
    public final String ACTION = "ColorPickerDialog.color";

    public ColorPickerDialog(ConfigActivity activity, int fieldId, int color) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        _initialColor = color;
        _fieldId = fieldId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.color_picker);
        _colorPickerView = findViewById(R.id.color_picker_view);
        _colorPickerView.setColor(_initialColor);
        _colorButton = findViewById(R.id.color_picker_button);
        _colorButton.setOnClickListener(_buttonClickListener);
    }

    View.OnClickListener _buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int color = _colorPickerView.getColor();
             _activity.setColor(_fieldId, color);
//            Intent intent = new Intent();
//            intent.setAction(ACTION);
//            intent.putExtra("color", color);
//            _activity.setResult(Activity.RESULT_OK, intent);
            dismiss();
        }
    };


}
