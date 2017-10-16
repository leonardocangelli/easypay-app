package br.com.easypayapp.easypay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import br.com.easypayapp.easypay.barcode.BarcodeCaptureActivity;
import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.cartao.CadastroCartaoActivity;
import br.com.easypayapp.easypay.garcom.AberturaMesaActivity;
import br.com.easypayapp.easypay.garcom.GarcomMainActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.loja.LojaProdutosActivity;
import br.com.easypayapp.easypay.mesa.MesaActivity;
import br.com.easypayapp.easypay.model.Pedido;
import br.com.easypayapp.easypay.model.Usuario;

public class MainActivity extends ComposeActivity {

    public String QRCODE_ENCODED = "";
    private Context mContext;
    boolean stop = false;
    private AlertDialog dialog;
    private Intent intentMesa;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkToken();
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        setTitle("");
        setBackButton(false);

        intentMesa = new Intent(mContext, MesaActivity.class);
    }

    public void abrirMesa(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        doRequestCheck(token, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;

                    startActivity(new Intent(this, LojaProdutosActivity.class));

                } else Toast.makeText(this, R.string.no_barcode_captured, Toast.LENGTH_SHORT).show();
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public void abrirLojas(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String n_cartao = preferences.getString(Constants.N_CARTAO, "");
        if (!n_cartao.equals("")) {
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Para pagar pelo EasyPay você precisa cadastrar seu cartão de crédito.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.create().show();
        }
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
        String idPerfil = preferences.getString(Constants.ID_PERFIL, null);

        if (token == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (token != null && !idPerfil.equalsIgnoreCase("6")) {
            startActivity(new Intent(MainActivity.this, GarcomMainActivity.class));
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
                stop = true;
            }
        });
        dialog = builder.create();
        dialog.setTitle("Apresente para o garçom.");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.qr_layout, null);
        dialog.setView(dialogLayout);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        ImageView img = (ImageView) dialog.findViewById(R.id.img);

        img.setImageBitmap(decodeBase64(qrcode));

        callAsynchronousTask();
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


    public void doRequestCheck(final String token, final String idUsuario) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int mesa_aberta = preferences.getInt(Constants.MESA_ABERTA, 0);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest (
                Request.Method.GET,
                Constants.ENDPOINT + "Pedido/BuscaPedidoAberto?idUsuario=" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        setMesaAberta(1);
                        pDialog.hide();
                        startActivity( intentMesa );

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            setMesaAberta(0);

                            if(mesa_aberta == 0) {
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

                            pDialog.hide();
                        } else {
                            setMesaAberta(0);

                            if(mesa_aberta == 0) {
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

                            pDialog.hide();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Token", token);
                headers.put("Id", idUsuario);
                return headers;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void doRequestCheckPedido(final String token, final String idUsuario) {

        StringRequest stringRequest = new StringRequest (
                Request.Method.GET,
                Constants.ENDPOINT + "Pedido/BuscaPedidoAberto?idUsuario=" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        setMesaAberta(1);
                        stop = true;
                        dialog.hide();
                        startActivity( intentMesa );


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            //Toast.makeText(mContext, mContext.getString(R.string.login_invalido), Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(mContext, mContext.getString(R.string.erro_request), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Token", token);
                headers.put("Id", idUsuario);
                return headers;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void callAsynchronousTask() {

        stop = false;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String token = preferences.getString(Constants.TOKEN, null);
        final String id = preferences.getString(Constants.ID, null);

        final Handler handler = new Handler();
        Timer timer = new Timer();
        final TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                if (stop) {
                    return;
                }
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            //IF RESPONSE VOLLEY -> stop = true;
                            doRequestCheckPedido(token, id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 2000);

    }

    public void setMesaAberta(int statusMesa) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.MESA_ABERTA, statusMesa);
        editor.commit();
    }

}
