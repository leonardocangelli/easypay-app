package br.com.easypayapp.easypay.garcom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.adapter.ListaAdapter;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.mesa.MesaActivity;
import br.com.easypayapp.easypay.mesa.MesaContaActivity;
import br.com.easypayapp.easypay.model.Produto;

public class DetalhesMesaActivity extends ComposeActivity {

    private TextView textNumeroMesa, textTotal;
    private Context mContext;
    private String idPedido = "";
    private ListView listView;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa_detalhes);
        setBackButton(true);
        mContext = getApplicationContext();
        setTitleMenu(mContext.getString(R.string.detalhes));
        initViews();

        idPedido = getIntent().getStringExtra("idPedido");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DetalhesMesaActivity.this);
        final String token = preferences.getString(Constants.TOKEN, null);
        final String idGarcom = preferences.getString(Constants.ID, null);

        getProdutos(token, idGarcom);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProdutos(token, idGarcom);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        textTotal = (TextView) findViewById(R.id.textTotal);
        textNumeroMesa = (TextView) findViewById(R.id.textNumeroMesa);
        String mesa = getIntent().getStringExtra("mesa");
        textNumeroMesa.setText(mContext.getString(R.string.mesa) + mesa);
    }

    public void adicionar(View view) {
        Intent intent = new Intent(mContext, AddProdutosActivity.class);
        intent.putExtra("idPedido", idPedido);
        startActivity(intent);
    }

    public void fechar(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(mContext.getString(R.string.fechar_conta));
        alert.setMessage(mContext.getString(R.string.fechar_conta_desejo));
        alert.setPositiveButton(mContext.getString(R.string.confirmar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DetalhesMesaActivity.this);
                final String token = preferences.getString(Constants.TOKEN, null);
                final String idUsuario = preferences.getString(Constants.ID, null);
                String idPedido = getIntent().getStringExtra("idPedido");
                doRequestFecharConta(idPedido, token, idUsuario);
            }
        });
        alert.setNegativeButton(mContext.getString(R.string.cancelar), new DialogInterface.OnClickListener() {
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
        alert.setTitle(mContext.getString(R.string.app_name));
        alert.setMessage(mContext.getString(R.string.conta_finalizada));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                startActivity(new Intent(DetalhesMesaActivity.this, MainActivity.class));
            }
        });
        alert.create().show();
    }

    public void getProdutos(final String token, final String idGarcom) {

        final ArrayList<Produto> listaProdutos = new ArrayList<>();

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "pedidoproduto/produtosdopedido?idpedido=" + idPedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            double total = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject produtos = jsonArray.getJSONObject(i);

                                Produto produto = new Produto();
                                produto.setId(produtos.getLong("Id"));
                                produto.setDescricao(produtos.getString("Descricao"));
                                produto.setPreco(produtos.getDouble("Preco"));
                                produto.setQuantidade(produtos.getInt("Quantidade"));
                                //produto.setTotal(produtos.getDouble("Total"));
                                total += produto.getTotal();
                                listaProdutos.add(produto);
                            }
                            ListAdapter adapter = new ListaAdapter(listaProdutos, mContext);
                            //ArrayAdapter<Produto> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, listaProdutos);
                            listView.setAdapter(adapter);
                            textTotal.setText("Total: R$ " + String.format("%.2f", (total * 1.20) ));
                        } catch (JSONException e) {
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
