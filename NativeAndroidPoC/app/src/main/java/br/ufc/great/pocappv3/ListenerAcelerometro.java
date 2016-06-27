package br.ufc.great.pocappv3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by messi on 16/10/2015.
 *
 * @author Messias Lima
 */
public class ListenerAcelerometro implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private MainActivity mainActivity;
    private boolean semaforo = true;

    public ListenerAcelerometro(Context context, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            Toast.makeText(mainActivity, R.string.accelerometroNaoDisponivel, Toast.LENGTH_LONG).show();
        }
    }

    public void iniciarMonitoramento() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pararMonitoramento() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (!mainActivity.isOcupado()) {
            float x = event.values[0];
            if (semaforo) {

                if (x > 6) {
                    mainActivity.imprimirImagem(mainActivity.indiceDaFoto - 1);
                    Log.i("direcao", "voltar");
                    semaforo = false;
                }
                if (x < -6) {
                    mainActivity.imprimirImagem(mainActivity.indiceDaFoto + 1);
                    Log.i("direcao", "ir");
                    semaforo = false;
                }
            } else {
                if (x < 2 && x > -2) {
                    semaforo = true;
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
