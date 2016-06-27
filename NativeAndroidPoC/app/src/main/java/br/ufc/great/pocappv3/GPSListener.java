package br.ufc.great.pocappv3;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;

/**
 * Created by messi on 20/10/2015.
 *
 * @author Messias Lima
 */
public class GPSListener implements LocationListener {
    private MainActivity context;
    private LocationManager locationManager = null;
    private boolean ativo = false;

    public boolean isAtivo() {
        return ativo;
    }

    private Location localizacaoAtual;

    public GPSListener(Context context) {
        this.context =(MainActivity) context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void iniciarMonitoramento() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        ativo = true;
    }

    public void pararMonitoramento() {
        locationManager.removeUpdates(this);
        ativo = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (localizacaoAtual==null){
            context.mostrarMensagem(context.getString(R.string.localizacaoDisponivel));
        }
        localizacaoAtual = location;
        Log.i("Location Changed","Latitude: "+ location.getLatitude()+ " Longitude: "+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocalizacaoAtual() {
        return localizacaoAtual;
    }
}
