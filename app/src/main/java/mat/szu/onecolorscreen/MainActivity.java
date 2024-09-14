package mat.szu.onecolorscreen;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity
{
    private static int lightnessValue = 255;
    private static int redValue = 85;
    private static int greenValue = 0;
    private static int blueValue = 0;
    private static String hexColor;
    private static GradientDrawable backgroundColorDrawable;
    private EditText hexValueEditText;
    private LinearLayout settings;
    private Slider lightnessSlider;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private boolean changeFromSlider = false;
    private boolean changeText = true;
    
    public static void applyLightness( )
    {
        int redColor   = (redValue * lightnessValue / 255);
        int greenColor = (greenValue * lightnessValue / 255);
        int blueColor  = (blueValue * lightnessValue / 255);
        
        int backgroundColor = Color.rgb(redColor, greenColor, blueColor);
        hexColor = String.format("#%06X", (0xFFFFFF&backgroundColor));
        backgroundColorDrawable.setColor(backgroundColor);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        View decorView = getWindow( ).getDecorView( );
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        hexValueEditText = findViewById(R.id.hex_color_input);
        lightnessSlider  = findViewById(R.id.lightness_bar);
        redSlider        = findViewById(R.id.red_bar);
        greenSlider      = findViewById(R.id.green_bar);
        blueSlider       = findViewById(R.id.blue_bar);
        settings         = findViewById(R.id.settings_layout);
        FrameLayout background = findViewById(R.id.color_layout);
        
        settings.setOnClickListener(v -> {});
        background.setOnLongClickListener(v -> changeSlidersPanelVisibility( ));
        lightnessSlider.addOnChangeListener((slider, value, fromUser) -> onSliderChange(slider, (int) value, 0));
        redSlider.addOnChangeListener((slider, value, fromUser) -> onSliderChange(slider, (int) value, 1));
        greenSlider.addOnChangeListener((slider, value, fromUser) -> onSliderChange(slider, (int) value, 2));
        blueSlider.addOnChangeListener((slider, value, fromUser) -> onSliderChange(slider, (int) value, 3));
        hexValueEditText.setOnEditorActionListener((v, actionId, event) -> clearFocusAndHideKeyboard(v));
        
        lightnessSlider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(lightnessValue, lightnessValue, lightnessValue)));
        backgroundColorDrawable = (GradientDrawable) background.getBackground( );
        applyLightness( );
        
        hexValueEditText.addTextChangedListener(new TextWatcher( )
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                onTextChange(s.toString( ));
            }
        });
    }
    
    private void onTextChange(String colorString)
    {
        if (changeFromSlider)
        {
            changeFromSlider = false;
            return;
        }
        
        if (colorString.startsWith("#"))
            colorString = colorString.substring(1);
        
        if (!colorString.matches("[0-9a-fA-F]+"))
            return;
        
        StringBuilder fullColor = new StringBuilder( );
        int           length    = colorString.length( );
        
        switch (length)
        {
            case 1:
                fullColor.append(colorString.charAt(0)).append(colorString.charAt(0)).append(colorString.charAt(0)).append(
                        colorString.charAt(0)).append(colorString.charAt(0)).append(colorString.charAt(0));
                break;
            case 2:
                fullColor.append(colorString.charAt(0)).append(colorString.charAt(1)).append(colorString.charAt(0)).append(
                        colorString.charAt(1)).append(colorString.charAt(0)).append(colorString.charAt(1));
                break;
            case 3:
                fullColor.append(colorString.charAt(0)).append(colorString.charAt(0)).append(colorString.charAt(1)).append(
                        colorString.charAt(1)).append(colorString.charAt(2)).append(colorString.charAt(2));
                break;
            case 6:
                fullColor.append(colorString);
                break;
            default:
                return;
        }
        
        hexColor = "#" + colorString;
        int inputColor = Color.parseColor("#" + fullColor);
        redValue       = Color.red(inputColor);
        greenValue     = Color.green(inputColor);
        blueValue      = Color.blue(inputColor);
        lightnessValue = 255;
        
        changeText = false;
        
        lightnessSlider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(lightnessValue, lightnessValue, lightnessValue)));
        redSlider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(redValue, 0, 0)));
        greenSlider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(0, greenValue, 0)));
        blueSlider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(0, 0, blueValue)));
        lightnessSlider.setValue(lightnessValue);
        redSlider.setValue(redValue);
        greenSlider.setValue(greenValue);
        blueSlider.setValue(blueValue);
        
        applyLightness( );
        
        changeText = true;
    }
    
    private boolean clearFocusAndHideKeyboard(TextView v)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken( ), 0);
        hexValueEditText.clearFocus( );
        return true;
    }
    
    private boolean changeSlidersPanelVisibility( )
    {
        if (settings.getVisibility( ) == View.GONE)
            settings.setVisibility(View.VISIBLE);
        else
            settings.setVisibility(View.GONE);
        return true;
    }
    
    public void onSliderChange(Slider slider, int value, int sliderNumber)
    {
        switch (sliderNumber)
        {
            default:
                lightnessValue = value;
                slider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(value, value, value)));
                break;
            case 1:
                redValue = value;
                slider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(value, 0, 0)));
                break;
            case 2:
                greenValue = value;
                slider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(0, value, 0)));
                break;
            case 3:
                blueValue = value;
                slider.setTrackActiveTintList(android.content.res.ColorStateList.valueOf(Color.rgb(0, 0, value)));
                break;
        }
        applyLightness( );
        if (changeText)
        {
            changeFromSlider = true;
            hexValueEditText.setText(hexColor);
        }
    }
    
}