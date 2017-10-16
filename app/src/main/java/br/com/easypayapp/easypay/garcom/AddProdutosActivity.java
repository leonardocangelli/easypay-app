package br.com.easypayapp.easypay.garcom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import java.util.List;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Pedido;
import br.com.easypayapp.easypay.model.Produto;
import br.com.easypayapp.easypay.model.ProdutoSpinner;

public class AddProdutosActivity extends ComposeActivity {

    private Context mContext;
    private Spinner spinnerProdutos;
    private EditText edtQtd, edtObservacao;
    private ArrayList<Produto> produtosPedido;
    private ArrayAdapter<ProdutoSpinner> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produto);
        setBackButton(true);
        mContext = getApplicationContext();
        setTitleMenu(mContext.getString(R.string.adicionar));
        initViews();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        getProdutos(token, id);
    }

    private void initViews() {
        spinnerProdutos = (Spinner) findViewById(R.id.spinnerProdutos);
        edtQtd = (EditText) findViewById(R.id.edtQtd);
        edtObservacao = (EditText) findViewById(R.id.edtObservacao);
        produtosPedido = new ArrayList<>();

        spinnerProdutos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    edtQtd.setEnabled(false);
                    edtObservacao.setEnabled(false);
                    edtQtd.setText("");
                    edtObservacao.setText("");
                } else {
                    edtQtd.setEnabled(true);
                    edtObservacao.setEnabled(true);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    public void inserir(View view) {
        //boolean filledObservacao = edtObservacao.getText().toString().trim().length() != 0;
        boolean filledQtd = edtQtd.getText().toString().trim().length() != 0;
        boolean filledProduto = spinnerProdutos.getSelectedItemPosition() != 0;

        if (filledQtd && filledProduto) {
            ProdutoSpinner produtoSpinner = (ProdutoSpinner) spinnerProdutos.getSelectedItem();

            Produto produto = new Produto();
            produto.setId(produtoSpinner.getId());
            produto.setDescricao(produtoSpinner.getDescricao());
            produto.setQuantidade(Integer.parseInt(edtQtd.getText().toString()));
            produto.setObservacao(edtObservacao.getText().toString());
            produto.setPreco(produtoSpinner.getPreco());

            int pos_equal = -1;
            for(int i=0; i<produtosPedido.size(); i++) {
                if (produtosPedido.get(i).getId() == produto.getId()) {
                    pos_equal = i;
                }
            }
            if (pos_equal != -1) {
                produtosPedido.get(pos_equal).setQuantidade(
                        produtosPedido.get(pos_equal).getQuantidade() + produto.getQuantidade()
                );
            } else {
                produtosPedido.add(produto);
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String token = preferences.getString(Constants.TOKEN, null);
            String id = preferences.getString(Constants.ID, null);

            doRequestInserir(id, token, getIntent().getStringExtra("idPedido"), produto);
            limparCampos();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.erro_campos), Toast.LENGTH_LONG).show();
        }
    }

    public void visualizarItens(View view) {
        Intent intent = new Intent(mContext, ProdutosActivity.class);
        intent.putParcelableArrayListExtra("produtos", produtosPedido);
        intent.putExtra("idPedido", getIntent().getStringExtra("idPedido"));
        startActivity(intent);
    }

    public void limparCampos() {
        edtObservacao.setText("");
        edtQtd.setText("");
        spinnerProdutos.setSelection(0);
    }

    public void getProdutos(final String token, final String idGarcom) {

        final List<ProdutoSpinner> listaProdutos = new ArrayList<>();
        listaProdutos.add(new ProdutoSpinner(mContext.getString(R.string.selecione)));

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "Produto",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject produtos = jsonArray.getJSONObject(i);

                            ProdutoSpinner produto = new ProdutoSpinner();
                            produto.setId(produtos.getLong("Id"));
                            produto.setDescricao(produtos.getString("Descricao"));
                            produto.setPreco(produtos.getDouble("Preco"));

                            listaProdutos.add(produto);
                        }
                           adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, listaProdutos);
                           spinnerProdutos.setAdapter(adapter);
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

    public void doRequestInserir(final String idGarcom, final String token, final String idPedido, final Produto produto) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "pedidoproduto/addprodutopedido",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.hide();
                        Toast.makeText(mContext, response.toString(), Toast.LENGTH_LONG).show();

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

                params.put("IdPedido", idPedido);
                params.put("IdProduto", Long.toString(produto.getId()));
                params.put("Quanditade", String.valueOf(produto.getQuantidade()));

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
