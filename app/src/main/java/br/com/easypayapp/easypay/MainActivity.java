package br.com.easypayapp.easypay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.cartao.CadastroCartaoActivity;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.mesa.MesaActivity;

public class MainActivity extends ComposeActivity {

    public String QRCODE_ENCODED = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkToken();
        setContentView(R.layout.activity_main);
        setBackButton(false);
    }

    public void abrirMesa(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Abrir Mesa");
        dialog.setMessage("Verificamos que você não possui mesa aberta. Deseja abrir?");
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String qrcode = preferences.getString(Constants.QRCODE, null);
                String id = preferences.getString(Constants.ID, null);

                if (qrcode == null) {
                    gerarQR(id);
                } else {
                    abrirDialog(qrcode);
                }

            }
        });
        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create().show();
    }

    public void abrirAmigos(View view) {
        Toast.makeText(this, "Amigos!", Toast.LENGTH_SHORT).show();
    }

    public void abrirHistorico(View view) {
        Toast.makeText(this, "Histórico!", Toast.LENGTH_SHORT).show();
    }

    public void abrirMeusDados(View view) {
        startActivity(new Intent(this, CadastroActivity.class));
    }

    private void checkToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);

        if (token == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    public void gerarQR(String param) {
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(param);
    }

    public void abrirDialog(String qrcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.qr_layout, null);
        dialog.setView(dialogLayout);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        ImageView img = (ImageView) dialog.findViewById(R.id.img);

        img.setImageBitmap(decodeBase64(qrcode));
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, MesaActivity.class));
            }
        });
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class BackgroundTask extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Gerando QR Code...");
            pd.show();
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            pd.dismiss();
            QRCODE_ENCODED = encodeTobase64(b);
            setQRPrefs(QRCODE_ENCODED);
            abrirDialog(QRCODE_ENCODED);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = TextToImageEncode(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void setQRPrefs(String qrcode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.QRCODE, qrcode);
        editor.commit();
    }

}
