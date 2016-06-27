package br.ufc.great.pocappv3;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;

import java.io.File;

/**
 * Created by messi on 20/10/2015.
 *
 * @author Messias Lima
 */
public class CentralDeCompartilhamento implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_LOGIN = 50;
    private static final int SHARE_CONTENT_LOCATION = 1;
    private static final int SHARE_CONTENT_IMAGEM = 2;


    private GoogleApiClient googleApiClient;
    private boolean intentTrabalhando;
    private Activity context;

    public CentralDeCompartilhamento(Activity context) {
        this.context = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentTrabalhando && connectionResult.hasResolution()) {
            try {
                intentTrabalhando = true;
                context.startIntentSenderForResult(connectionResult.getResolution().getIntentSender(), REQUEST_CODE_LOGIN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                intentTrabalhando = false;
                googleApiClient.connect();
            }
        }
    }

    public void compartilhar(Location location) {
        compartilhar(SHARE_CONTENT_LOCATION, location, null);
    }

    public void compartilhar(File imagem) {
        compartilhar(SHARE_CONTENT_IMAGEM, null, imagem);
    }

    private void compartilhar(int tipo, Location location, File imagem) {

        PlusShare.Builder shareBuilder = new PlusShare.Builder(context);
        shareBuilder.setText("TestepocAPP");

        if (tipo == SHARE_CONTENT_IMAGEM) {
            Uri imagemUri = Uri.fromFile(imagem);
            shareBuilder.setType("image/*")
                    .addStream(imagemUri);
        } else {
            shareBuilder.setType("text/*")
                    .setContentUrl(Uri.parse("https://maps.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude()));
        }

        Intent shareIntent = shareBuilder.getIntent();
        context.startActivityForResult(shareIntent, 0);
    }
}
