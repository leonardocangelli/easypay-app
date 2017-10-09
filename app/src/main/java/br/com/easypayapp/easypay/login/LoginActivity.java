package br.com.easypayapp.easypay.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.garcom.GarcomMainActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;

public class LoginActivity extends BaseActivity {

    private EditText edit_text_email, edit_text_password;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();

        initViews();
    }

    private void setTokenPrefs(String token, String id, String n_cartao, String idPerfil) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.putString(Constants.ID, id);
        editor.putString(Constants.ID_PERFIL, idPerfil);
        editor.putString(Constants.N_CARTAO, n_cartao);
        editor.commit();
    }

    private void initViews() {
        edit_text_email = (EditText) findViewById(R.id.edit_text_email);
        edit_text_password = (EditText) findViewById(R.id.edit_text_password);
    }

    public boolean isAllFilled() {
        boolean filledEmail = edit_text_email.getText().toString().trim().length() != 0;
        boolean filledPassword = edit_text_password.getText().toString().trim().length() != 0;

        return filledEmail & filledPassword;
    }

    public void logar(View view) {
        String email = edit_text_email.getText().toString();
        String senha = edit_text_password.getText().toString();

        if (isAllFilled())
            doRequestLogin(email, senha);
        else
            Toast.makeText(mContext, mContext.getString(R.string.erro_campos), Toast.LENGTH_LONG).show();


    }

    public void abrirCadastro(View view) {
        Intent intent = new Intent(mContext, CadastroActivity.class);
        startActivity(intent);
    }

    private void doRequestLogin(final String email, final String senha) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "usuario/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        String token = obj.get("Token").getAsString();
                        String id = obj.get("Id").getAsString();
                        JsonArray array = obj.getAsJsonArray("Cartao");
                        String idPerfil = obj.get("IdPerfil").getAsString();
                        String n_cartao = "";

                        if (array.size() != 0) {
                            n_cartao = array.get(0).getAsJsonObject().get("Numero").getAsString();
                        }

                        setTokenPrefs(token, id, n_cartao, idPerfil);

                        if (idPerfil.equalsIgnoreCase("6")) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            String idEmpresa = obj.get("IdEmpresa").getAsString();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constants.ID_EMPRESA, idEmpresa);
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this, GarcomMainActivity.class));
                        }

                        finish();

                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            Toast.makeText(LoginActivity.this, mContext.getString(R.string.login_invalido), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, mContext.getString(R.string.erro_request), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Email", email);
                params.put("Senha", senha);
                return params;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}