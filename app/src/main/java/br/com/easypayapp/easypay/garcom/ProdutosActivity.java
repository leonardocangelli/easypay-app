package br.com.easypayapp.easypay.garcom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Produto;

public class ProdutosActivity extends ComposeActivity {

    private Context mContext;
    private ListView listProdutos;
    private ArrayAdapter<Produto> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_produtos);
        setBackButton(true);
        setTitleMenu("Pedido");
        mContext = getApplicationContext();
        initViews();
    }

    private void initViews() {
        listProdutos = (ListView) findViewById(R.id.listProdutos);
        ArrayList<Produto> produtos = this.getIntent().getParcelableArrayListExtra("produtos");
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, produtos);
        listProdutos.setAdapter(adapter);
    }

    public void finalizar(View view) {
        //finishAffinity();
        //startActivity(new Intent(this, GarcomMainActivity.class));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String token = preferences.getString(Constants.TOKEN, null);
        String idGarcom = preferences.getString(Constants.ID, null);
        String idPedido = "26";



        doRequestFinalizar(idGarcom, token, idPedido);
    }

    public void doRequestFinalizar(final String idGarcom, final String token, final String idPedido) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "pedidoproduto/addprodutopedido",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();
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


                for(listProdutos)

                params.put("IdPedido", idPedido);
                params.put("IdProduto", "1");
                params.put("Quanditade", "2");

                params.put("IdPedido", idPedido);
                params.put("IdProduto", "2");
                params.put("Quanditade", "5");

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Token", token);
                headers.put("Id", idGarcom);
                return headers;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }
}
