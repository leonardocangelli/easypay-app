package br.com.easypayapp.easypay.cartao;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.model.Cartao;

public class CadastroCartaoActivity extends BaseActivity {

    private EditText edit_text_nome,
            edit_text_numero,
            edit_text_mes,
            edit_text_ano,
            edit_text_cvv;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cartao);
        mContext = getApplicationContext();
        initViews();
    }

    private void initViews() {
        edit_text_nome = (EditText) findViewById(R.id.edit_text_nome);
        edit_text_numero = (EditText) findViewById(R.id.edit_text_numero);
        edit_text_mes = (EditText) findViewById(R.id.edit_text_mes);
        edit_text_ano = (EditText) findViewById(R.id.edit_text_ano);
        edit_text_cvv = (EditText) findViewById(R.id.edit_text_cvv);
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

        doRequestSalvarCartao(cartao);
    }

    private void doRequestSalvarCartao(final Cartao cartao) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "cartao/cadastrar",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        Toast.makeText(CadastroCartaoActivity.this, response, Toast.LENGTH_SHORT).show();
                        //finish();
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
                params.put("DataVencimento", cartao.getDataVencimento().toString());
                params.put("CodSeguranca", cartao.getCvv());
                params.put("IdUsuario", "17");
                return params;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }
}