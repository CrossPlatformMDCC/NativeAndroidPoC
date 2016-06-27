package br.ufc.great.pocappv3;

import android.app.Activity;
import android.content.Intent;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by messi on 16/10/2015.
 *
 * @author Messias Lima
 */

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_FOTO = 654;

    private ImageView imageView;
    private List<File> imagens = new ArrayList<File>();
    private File imagemAtual;
    public int indiceDaFoto = 0;
    private TextView contadorDeImagens;
    private ListenerAcelerometro listenerAcelerometro;
    private GPSListener gpsListener;
    //private Button gpsButton;
    private CentralDeCompartilhamento centralDeCompartilhamento;
    private boolean ocupado = false;

    public boolean isOcupado() {
        return ocupado;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        iniciarComponentes();
        iniciarAcelerometro();
        iniciarCentralDeCompartilhamento();
        manipularGPS(null);
    }

    public void uploadImagem(View view) {
        if (imagens.size() > 0) {
            UploadImagem uploadImagem = new UploadImagem(this);
            uploadImagem.execute(imagens.get(indiceDaFoto));
        } else {
            mostrarMensagem(getString(R.string.tirarFoto));
        }
    }

    private void iniciarCentralDeCompartilhamento() {
        centralDeCompartilhamento = new CentralDeCompartilhamento(this);
    }

    public void compartilharLocalizacao(View view) {
        if (gpsListener == null) {
            mostrarMensagem(getString(R.string.ativeGPS));
        } else {
            Location location = gpsListener.getLocalizacaoAtual();
            if (location == null) {
                mostrarMensagem(getString(R.string.localizacaoNaoDisponivel));
            } else {
                centralDeCompartilhamento.compartilhar(location);
            }
        }
    }

    public void compartilharImagem(View view) {
        if (imagens.size() == 0) {
            mostrarMensagem(getString(R.string.tirarFoto));
        } else {
            centralDeCompartilhamento.compartilhar(imagens.get(indiceDaFoto));
        }
    }

    public void manipularGPS(View view) {
        if (gpsListener != null && gpsListener.isAtivo()) {
            pararGPS();
        } else {
            iniciarGPS();
        }
    }

    private void iniciarGPS() {
        if (gpsListener == null) {
            gpsListener = new GPSListener(this);
        }
        gpsListener.iniciarMonitoramento();
//        gpsButton.setText(R.string.parar);
    }

    private void pararGPS() {
        if (gpsListener!=null && gpsListener.isAtivo()) {
            gpsListener.pararMonitoramento();
            //gpsButton.setText(R.string.iniciar);
        }
    }

    public void iniciarAcelerometro() {
        listenerAcelerometro = new ListenerAcelerometro(this, this);
        listenerAcelerometro.iniciarMonitoramento();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerAcelerometro.pararMonitoramento();
        pararGPS();
    }

    public void iniciarComponentes() {
        imageView = (ImageView) findViewById(R.id.imageview);
        contadorDeImagens = (TextView) findViewById(R.id.contador_de_imagens);
        //gpsButton = (Button) findViewById(R.id.gps_button);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FOTO) {
            if (resultCode == RESULT_OK) {
                imagens.add(imagemAtual);
                indiceDaFoto = imagens.size() - 1;
                imprimirImagem(null);
            }
        }
    }

    public void tirarFoto(View view) {
        try {
            if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile().exists()){
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile().mkdir();
            }

            imagemAtual = File.createTempFile(getString(R.string.app_name), ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagemAtual));
            startActivityForResult(intent, REQUEST_CODE_FOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imprimirImagem(Integer indice) {
        imageView.setVisibility(View.VISIBLE);
        ocupado = true;
        if (imagens.size() > 0) {
            if (indice == null) {
                if (imagemAtual != null) {
                    imageView.setImageURI(Uri.fromFile(imagemAtual));
                }
            } else {
                if (indice < 0) {
                    indice = imagens.size() - 1;

                }
                if (indice > imagens.size() - 1) {
                    indice = 0;
                }
                indiceDaFoto = indice;
                if (!imagens.get(indice).canRead()) {
                    imagens.get(indice).delete();
                    atualizarContadorDeImagens();
                    return;
                }
                imageView.setImageURI(Uri.fromFile(imagens.get(indice)));
            }
            atualizarContadorDeImagens();
        }
        ocupado = false;
    }

    public void resetarImagem(View view) {
        resetarFiltroImageView();
    }

    public void atualizarContadorDeImagens() {
        contadorDeImagens.setText( (indiceDaFoto + 1) + " de " + imagens.size());
    }

    public void inverterCores(View view) {
        resetarFiltroImageView();
        float[] colorMatrix_Negativo = {
                -1.0f, 0, 0, 0, 255, //vermelho
                0, -1.0f, 0, 0, 255, //verde
                0, 0, -1.0f, 0, 255, //azul
                0, 0, 0, 1.0f, 0 //alpha
        };

        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negativo);
        imageView.setColorFilter(colorFilter_Negative);
    }

    public void aplicarSepia(View view) {
        resetarFiltroImageView();
        float[] colorMatrixSepia = {
                0.3930000066757202f, 0.7689999938011169f, 0.1889999955892563f, 0, 0,
                0.3490000069141388f, 0.6859999895095825f, 0.1679999977350235f, 0, 0,
                0.2720000147819519f, 0.5339999794960022f, 0.1309999972581863f, 0, 0,
                0, 0, 0, 1, 0,
                0, 0, 0, 0, 1};//branco


        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrixSepia);
        imageView.setColorFilter(colorFilter_Negative);
    }

    public void aplicarGreyscale(View view) {
        resetarFiltroImageView();

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }

    public void mostrarMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private void resetarFiltroImageView() {
        imageView.setColorFilter(null);
    }
}
