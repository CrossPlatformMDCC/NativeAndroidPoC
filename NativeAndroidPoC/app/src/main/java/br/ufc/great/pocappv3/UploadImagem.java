package br.ufc.great.pocappv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.app.AlertDialog;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by messi on 20/10/2015.
 *
 * @author Messias Lima
 */
public class UploadImagem extends AsyncTask<File, Void, Boolean> {
    private Context context;
    private ProgressDialog progressDialog;

    public UploadImagem(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.uploadImagem);
        progressDialog.setMessage(context.getString(R.string.processando));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(File... params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://loccam.great.ufc.br/downloadFiles/upload/recebeUpload.php");
            File arquivo = params[0];

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntityBuilder.addBinaryBody("arquivo", arquivo, ContentType.DEFAULT_BINARY, arquivo.getName());

            httpPost.setEntity(multipartEntityBuilder.build());

            HttpResponse response = httpClient.execute(httpPost);

            String httpResponse = EntityUtils.toString(response.getEntity(), "UTF-8");

            Log.i("Resposta ", httpResponse);

            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Imagem Uploaded Successfully!");
        builder.setCancelable(false)
                .setNegativeButton("ok",null)
                .create().show();

        if (aBoolean) {
            ((MainActivity) context).mostrarMensagem(context.getString(R.string.compartilhamentoSucesso));
        } else {
            ((MainActivity) context).mostrarMensagem(context.getString(R.string.ocorreuUmErro));
        }
    }
}
