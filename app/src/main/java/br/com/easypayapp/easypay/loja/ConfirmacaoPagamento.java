package br.com.easypayapp.easypay.loja;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Cartao;

public class ConfirmacaoPagamento extends ComposeActivity {

    private Context mContext;
    private TextView textNome, textNumero, textExpiration, textCvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pagamento);
        setTitle("Dados do Cartão");
        setBackButton(true);
        mContext = getApplicationContext();
        initViews();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        getDados(token, id);
    }

    public void initViews() {
        textNome = (TextView) findViewById(R.id.textNome);
        textNumero = (TextView) findViewById(R.id.textNumero);
        textExpiration = (TextView) findViewById(R.id.textExpiration);
        textCvv = (TextView) findViewById(R.id.textCVV);
    }

    public void confirmarPagamento(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ConfirmacaoPagamento.this);
        dialog.setTitle("EasyPay");
        dialog.setMessage("Pagamento realizado com sucesso!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ConfirmacaoPagamento.this, MainActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });
        dialog.create().show();
    }

    public void getDados(final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "cartao/BuscarPorUsuario?id=" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        String nome = obj.get("NomeTitular").getAsString();
                        String numero = obj.get("Numero").getAsString();
                        String dataVencimento = obj.get("DataVencimento").getAsString();
                        String codSeguranca = obj.get("CodSeguranca").getAsString();
                        final String id = obj.get("Id").getAsString();

                        textNome.setText(nome);
                        String numeroFormat = numero.substring(0,4) + " " +
                                              numero.substring(4,8) + " " +
                                              numero.substring(8,12) + " " +
                                              numero.substring(12,16);
                        textNumero.setText(numeroFormat);
                        textExpiration.setText(dataVencimento.substring(2, 4) + "/" + dataVencimento.substring(5, 7));
                        textCvv.setText(codSeguranca);

                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            Toast.makeText(mContext, mContext.getString(R.string.login_invalido), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.erro_request), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("IdUsuario", idUsuario);
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
}
