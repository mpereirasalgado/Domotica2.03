package com.example.micael.domotica20;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;

import android.hardware.Camera;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;




public class Domotica extends AppCompatActivity {
//commit
    int contador=0;
    private Button botonencendido;
    private Camera camera;
    private TextView porcentaje;
    private SeekBar seekBar;
    private int brillo;
    private ContentResolver cResolver;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domotica);
        botonencendido = (Button) findViewById(R.id.botonLuz);
        botonencendido.setText("PULSAR");
        botonencendido.setBackgroundColor(Color.BLUE);
        seekBar = (SeekBar) findViewById(R.id.intensity);
        seekBar.setVisibility(View.INVISIBLE);
        seekBar.setMax(255);
        seekBar.setKeyProgressIncrement(1);
        cResolver = getContentResolver();
        window = getWindow();
        porcentaje = (TextView) findViewById(R.id.intens);


        try {
            brillo = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Settings.SettingNotFoundException e){
            Log.e("Error","Cannot access system brihtness");
            e.printStackTrace();
        }

        seekBar.setProgress(brillo);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<=20){
                    brillo=20;
                }else{
                    brillo=progress;
                }
                float perc = (brillo/(float)255)*100;
                porcentaje.setText((int) perc + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brillo);

                WindowManager.LayoutParams layoutpars = window.getAttributes();

                layoutpars.screenBrightness = brillo / (float)255;

                window.setAttributes(layoutpars);

            }
        });
    }


    public void clickBotonLuz(View v){
        contador++;
        Vibrator vib = (Vibrator) Domotica.this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(100);
        if(contador %2 == 0) {
            Toast.makeText(getBaseContext(), "APAGADO", Toast.LENGTH_SHORT).show();
            botonencendido.setText("OFF");
            botonencendido.setBackgroundColor(Color.RED);
            seekBar.setVisibility(View.INVISIBLE);
            camera.stopPreview();
            camera.release();
            porcentaje.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(getBaseContext(), "ENCENDIDO", Toast.LENGTH_SHORT).show();
            botonencendido.setText("ON");
            botonencendido.setBackgroundColor(Color.GREEN);
            seekBar.setVisibility(View.VISIBLE);
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            porcentaje.setVisibility(View.VISIBLE);
        }
    }

}
