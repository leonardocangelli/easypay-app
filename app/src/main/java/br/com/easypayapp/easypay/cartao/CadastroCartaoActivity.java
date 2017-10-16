package br.com.easypayapp.easypay.cartao;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.model.Cartao;
import br.com.easypayapp.easypay.model.Usuario;

public class CadastroCartaoActivity extends ComposeActivity {

    private EditText edit_text_nome,
            edit_text_numero,
            edit_text_mes,
            edit_text_ano,
            edit_text_cvv;

    private Button btnCadastrar, btnCadastrarDepois;

    private Usuario usuario;
    private Context mContext;

    public String ENDPOINT_CADASTRAR_ALTERAR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cartao);
        mContext = getApplicationContext();
        initViews();
    }

    private void initViews() {
        usuario = (Usuario) getIntent().getSerializableExtra("Usuario");
        edit_text_nome = (EditText) findViewById(R.id.edit_text_nome);
        edit_text_numero = (EditText) findViewById(R.id.edit_text_numero);
        edit_text_mes = (EditText) findViewById(R.id.edit_text_mes);
        edit_text_ano = (EditText) findViewById(R.id.edit_text_ano);
        edit_text_cvv = (EditText) findViewById(R.id.edit_text_cvv);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnCadastrarDepois = (Button) findViewById(R.id.btnCadastrarDepois);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        Intent intent = getIntent();

        if (token != null && intent.getExtras() == null) {
            setHideActionBar(false);
            setBackButton(true);
            setTitleMenu(mContext.getString(R.string.meu_cartao));
            btnCadastrar.setText(mContext.getString(R.string.gravar));
            btnCadastrarDepois.setVisibility(View.GONE);
            getDados(token, id);
        } else {
            setHideActionBar(true);
        }

    }

    public boolean isAllFilled() {
        boolean filledNome = edit_text_nome.getText().toString().trim().length() != 0;
        boolean filledNumero = edit_text_numero.getText().toString().trim().length() != 0 &&
                               edit_text_numero.getText().toString().trim().length() == 16;
        boolean filledMes = edit_text_mes.getText().toString().trim().length() != 0;
        boolean filledAno = edit_text_ano.getText().toString().trim().length() != 0;
        boolean filledCvv = edit_text_cvv.getText().toString().trim().length() != 0;

        return filledNome & filledNumero & filledMes & filledAno & filledCvv;
    }

    public void cadastrarCartao(View view) {
        String nome = edit_text_nome.getText().toString();
        String numero = edit_text_numero.getText().toString();
        String mes = edit_text_mes.getText().toString();
        String ano = edit_text_ano.getText().toString();
        String cvv = edit_text_cvv.getText().toString();

        Cartao cartao = new Cartao();
        cartao.setNome(nome);
        cartao.setNumero(numero);
        cartao.setMesVencimento(mes);
        cartao.setAnoVencimento(ano);
        cartao.setCvv(cvv);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CadastroCartaoActivity.this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        if (isAllFilled()) {
            doRequestSalvarCartao(cartao, token, id);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.erro_campos), Toast.LENGTH_LONG).show();
        }

    }

    public void cadastrarDepois(View view) {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
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
                        try {
                            if (response.equals("null")) {
                                ENDPOINT_CADASTRAR_ALTERAR = "cartao/cadastrar";
                            } else {
                                JsonParser parser = new JsonParser();
                                JsonObject obj = parser.parse(response).getAsJsonObject();

                                String nome = obj.get("NomeTitular").getAsString();
                                String numero = obj.get("Numero").getAsString();
                                String dataVencimento = obj.get("DataVencimento").getAsString();
                                String codSeguranca = obj.get("CodSeguranca").getAsString();
                                final String id = obj.get("Id").getAsString();

                                edit_text_nome.setText(nome);
                                edit_text_numero.setText(numero);
                                edit_text_cvv.setText(codSeguranca);
                                edit_text_ano.setText(dataVencimento.substring(2, 4));
                                edit_text_mes.setText(dataVencimento.substring(5, 7));

                                ENDPOINT_CADASTRAR_ALTERAR = "cartao/alterar?id=" + id;
                                btnCadastrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Cartao c = new Cartao();
                                        c.setNome(edit_text_nome.getText().toString());
                                        c.setNumero(edit_text_numero.getText().toString());
                                        c.setCvv(edit_text_cvv.getText().toString());
                                        c.setAnoVencimento(edit_text_ano.getText().toString());
                                        c.setMesVencimento(edit_text_mes.getText().toString());

                                        if (isAllFilled()) {
                                            doRequestAlterarCartao(c, token, id, idUsuario);
                                        } else {
                                            Toast.makeText(mContext, mContext.getString(R.string.erro_campos), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }

                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


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

    public void doRequestSalvarCartao(final Cartao cartao, final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "cartao/cadastrar",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString(Constants.N_CARTAO, cartao.getNumero());
                        editor.commit();

                        startActivity(new Intent(mContext, MainActivity.class));
                        finish();

                        pDialog.hide();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(CadastroCartaoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("NomeTitular", cartao.getNome());
                params.put("Numero", cartao.getNumero());
                params.put("DataEntrada", cartao.getDataVencimento());
                params.put("CodSeguranca", cartao.getCvv());
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

    public void doRequestAlterarCartao(final Cartao cartao, final String token, final String id, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                Constants.ENDPOINT + "cartao/alterar?id=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString(Constants.N_CARTAO, cartao.getNumero());
                        editor.commit();

                        pDialog.hide();
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(CadastroCartaoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("NomeTitular", cartao.getNome());
                params.put("Numero", cartao.getNumero());
                params.put("DataEntrada", cartao.getDataVencimento());
                params.put("CodSeguranca", cartao.getCvv());
                params.put("Id", id);
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