package br.com.easypayapp.easypay.garcom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cartao.CadastroCartaoActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Cartao;

public class AberturaMesaActivity extends ComposeActivity {

    private TextView textNome, textCPF, textEmail;
    private EditText edtMesa;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_mesa);
        setBackButton(true);
        mContext = getApplicationContext();
        setTitleMenu(mContext.getString(R.string.abertura_mesa));

        initViews();

        String idCliente = getIntent().getStringExtra("idCliente");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        getDadosUsuario(token, id, idCliente);
    }

    private void initViews() {
        textNome = (TextView) findViewById(R.id.textNome);
        textCPF = (TextView) findViewById(R.id.textCPF);
        textEmail = (TextView) findViewById(R.id.textEmail);
        edtMesa =  (EditText) findViewById(R.id.edtMesa);
    }

    public void getDadosUsuario(final String token, final String idUsuario, final String idCliente) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "usuario/" + idCliente,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        String nome = obj.get("Nome").getAsString();
                        String email = obj.get("Email").getAsString();
                        String cpf = obj.get("Cpf").getAsString();

                        textNome.setText(nome);
                        textEmail.setText(email);
                        textCPF.setText(cpf);

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

    public void abrir(View view) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        String idCliente = getIntent().getStringExtra("idCliente");

        boolean filledMesa = edtMesa.getText().toString().trim().length() != 0;
        if (filledMesa) {
            doRequestAbrirMesa(id, token, idCliente, edtMesa.getText().toString());
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.numero_mesa), Toast.LENGTH_LONG).show();
        }

    }

    public void doRequestAbrirMesa(final String id, final String token, final String idCliente, final String mesa) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "Pedido/AbrirConta",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        String idPedido = obj.get("Id").getAsString();

                        pDialog.hide();

                        // CHAMAR ENDPOINT ABRIR MESA
                        Intent intent = new Intent(mContext, DetalhesMesaActivity.class);
                        intent.putExtra("nome", textNome.getText().toString());
                        intent.putExtra("mesa", edtMesa.getText().toString());
                        intent.putExtra("idPedido", idPedido);
                        startActivity(intent);
                        finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idcliente", idCliente);
                params.put("mesa", mesa);
                params.put("couver", "10");
                params.put("txservico", "10");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Token", token);
                headers.put("Id", id);
                return headers;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}
