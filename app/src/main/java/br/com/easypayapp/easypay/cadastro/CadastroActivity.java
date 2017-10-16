package br.com.easypayapp.easypay.cadastro;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cartao.CadastroCartaoActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.model.Usuario;

public class CadastroActivity extends ComposeActivity {

    private EditText edit_text_nome,
                    edit_text_email,
                    edit_text_telefone,
                    edit_text_cpf,
                    edit_text_senha;

    private Button btnContinuar, btnAlterarCartao;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        mContext = getApplicationContext();
        initViews();
    }


    private void initViews() {
        edit_text_nome = (EditText) findViewById(R.id.edit_text_nome);
        edit_text_email = (EditText) findViewById(R.id.edit_text_email);
        edit_text_telefone = (EditText) findViewById(R.id.edit_text_fone);
        edit_text_cpf = (EditText) findViewById(R.id.edit_text_cpf);
        edit_text_senha = (EditText) findViewById(R.id.edit_text_password);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnAlterarCartao = (Button) findViewById(R.id.btnAlterarCartao);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        if (token != null) {
            setHideActionBar(false);
            setBackButton(true);
            setTitleMenu(mContext.getString(R.string.meus_dados_string));
            btnContinuar.setText(mContext.getString(R.string.gravar));
            btnAlterarCartao.setVisibility(View.VISIBLE);
            getDados(token, id);
        } else {
            setHideActionBar(true);
        }
    }

    public void alterarCartao(View view) {
        startActivity(new Intent(mContext, CadastroCartaoActivity.class));
    }

    public void continuar(View view) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        String nome = edit_text_nome.getText().toString();
        String email = edit_text_email.getText().toString();
        String telefone = edit_text_telefone.getText().toString();
        String cpf = edit_text_cpf.getText().toString();
        String senha = edit_text_senha.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setSenha(senha);

        if (isAllFilled()) {
            if (token == null) {
                doRequestCadastro(usuario);
            } else {
                doRequestEdicao(usuario, token, id);
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.erro_campos), Toast.LENGTH_LONG).show();
        }

    }

    public void setTokenPrefs(String token, String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.putString(Constants.ID, id);
        editor.putString(Constants.ID_PERFIL, "6");
        editor.commit();
    }

    public boolean isAllFilled() {
        boolean filledNome = edit_text_nome.getText().toString().trim().length() != 0;
        boolean filledEmail = edit_text_email.getText().toString().trim().length() != 0;
        boolean filledTelefone = edit_text_telefone.getText().toString().trim().length() != 0;
        boolean filledCpf = edit_text_cpf.getText().toString().trim().length() != 0 &&
                            edit_text_cpf.getText().toString().trim().length() == 11;
        boolean filledSenha = edit_text_senha.getText().toString().trim().length() != 0;

        return filledNome & filledEmail & filledTelefone & filledCpf & filledSenha;
    }

    public void getDados(final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "usuario/" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        String nome = obj.get("Nome").getAsString();
                        String email = obj.get("Email").getAsString();
                        String telefone = obj.get("Telefone").getAsString();
                        String cpf = obj.get("Cpf").getAsString();
                        String senha = obj.get("Senha").getAsString();

                        edit_text_nome.setText(nome);
                        edit_text_email.setText(email);
                        edit_text_telefone.setText(telefone);
                        edit_text_cpf.setText(cpf);
                        edit_text_senha.setText(senha);

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Token", token);
                headers.put("Id", idUsuario);
                return headers;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void doRequestCadastro(final Usuario usuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "usuario",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        doRequestLogin(usuario, pDialog);

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
                params.put("Nome", usuario.getNome());
                params.put("Email", usuario.getEmail());
                params.put("Telefone", usuario.getTelefone());
                params.put("Cpf", usuario.getCpf());
                params.put("Senha", usuario.getSenha());
                return params;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void doRequestEdicao(final Usuario usuario, final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                Constants.ENDPOINT + "usuario/" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.hide();
                        finish();

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
                params.put("Id", idUsuario);
                params.put("Nome", usuario.getNome());
                params.put("Email", usuario.getEmail());
                params.put("Telefone", usuario.getTelefone());
                params.put("Cpf", usuario.getCpf());
                params.put("Senha", usuario.getSenha());
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

    public void doRequestLogin(final Usuario usuario, final ProgressDialog pDialog) {

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

                        setTokenPrefs(token, id);

                        Intent intent = new Intent(mContext, CadastroCartaoActivity.class);
                        intent.putExtra("Usuario", usuario);
                        intent.putExtra("Cadastro", "cadastro");

                        startActivity(intent);
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
                params.put("Email", usuario.getEmail());
                params.put("Senha", usuario.getSenha());
                return params;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}
