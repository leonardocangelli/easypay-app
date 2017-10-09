package br.com.easypayapp.easypay.garcom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.mesa.MesaContaActivity;

public class DetalhesMesaActivity extends ComposeActivity {

    private TextView textNumeroMesa;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa_detalhes);
        setBackButton(true);
        setTitleMenu("Detalhes");
        mContext = getApplicationContext();
        initViews();
    }

    private void initViews() {
        textNumeroMesa = (TextView) findViewById(R.id.textNumeroMesa);
        String mesa = getIntent().getStringExtra("mesa");
        textNumeroMesa.setText("Mesa: " + mesa);
    }

    public void adicionar(View view) {
        //startActivityForResult(mContext, AddProdutosActivity.class);

        //intent.putExtra("produto", produto);
        //setResult(RESULT_OK, intent);
        //finish();

        startActivity(new Intent(mContext, AddProdutosActivity.class));
    }

    public void fechar(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Fechar Conta");
        alert.setMessage("Deseja fechar a conta desta mesa?");
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DetalhesMesaActivity.this);
                final String token = preferences.getString(Constants.TOKEN, null);
                final String idUsuario = preferences.getString(Constants.ID, null);
                String idPedido = getIntent().getStringExtra("idPedido");
                doRequestFecharConta(idPedido, token, idUsuario);
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            data.getExtras().get("produto");
        }
    }

    public void doRequestFecharConta(final String idPedido, final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                Constants.ENDPOINT + "Pedido/AtualizarPedido",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.hide();
                        finalizarConta();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(mContext, mContext.getString(R.string.erro_request), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("IdStatus", "2");
                params.put("Id", idPedido);
                return params;
            }
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

    public void finalizarConta() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("EasyPay");
        alert.setMessage("Conta finalizada com sucesso!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                startActivity(new Intent(DetalhesMesaActivity.this, MainActivity.class));
            }
        });
        alert.create().show();
    }
}
