package br.com.easypayapp.easypay.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cadastro.CadastroActivity;
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

    private void setTokenPrefs(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.commit();
    }

    private void initViews() {
        edit_text_email = (EditText) findViewById(R.id.edit_text_email);
        edit_text_password = (EditText) findViewById(R.id.edit_text_password);
    }

    public void logar(View view) {
        String email = edit_text_email.getText().toString();
        String senha = edit_text_password.getText().toString();
        doRequestLogin(email, senha);
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
                        setTokenPrefs("tokenx");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, mContext.getString(R.string.login_invalido), Toast.LENGTH_SHORT).show();
                        pDialog.hide();
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