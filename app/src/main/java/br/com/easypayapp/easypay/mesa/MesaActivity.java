package br.com.easypayapp.easypay.mesa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Pedido;
import br.com.easypayapp.easypay.model.Produto;
import br.com.easypayapp.easypay.model.Usuario;

public class MesaActivity extends ComposeActivity {

    private Context mContext;
    private TextView textGarcom, textMesa, textTxServico, textCouver, textTotal;
    private ListView listViewProdutos;

    private double intentTaxa=0, intentCouver=0, intentValorTotal=0;
    private String intentGarcom, intentMesa;
    private Long intentIdPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa);
        mContext = getApplicationContext();
        setTitleMenu("Minha Mesa");
        setBackButton(true);

        initViews();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);

        doRequestCheckPedido(token, id);
    }

    public void initViews() {
        listViewProdutos = (ListView) findViewById(R.id.listView);
        textGarcom = (TextView) findViewById(R.id.textGarcom);
        textMesa = (TextView) findViewById(R.id.textMesa);
        textTxServico = (TextView) findViewById(R.id.textTxServ);
        textCouver = (TextView) findViewById(R.id.textCouver);
        textTotal = (TextView) findViewById(R.id.textTotal);
    }

    public void fecharConta(View view) {
        Intent intent = new Intent(mContext, MesaContaActivity.class);
        intent.putExtra("total", intentValorTotal);
        intent.putExtra("taxa", intentTaxa);
        intent.putExtra("couver", intentCouver);
        intent.putExtra("garcom", intentGarcom);
        intent.putExtra("mesa", intentMesa);
        intent.putExtra("idPedido", intentIdPedido);
        startActivity(intent);
    }

    public void doRequestCheckPedido(final String token, final String idUsuario) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();
        final ArrayList<Produto> produtos = new ArrayList<>();

        StringRequest stringRequest = new StringRequest (
                Request.Method.GET,
                Constants.ENDPOINT + "Pedido/BuscaPedidoAberto?idUsuario=" + idUsuario,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JsonParser parser = new JsonParser();
                        JsonObject obj = parser.parse(response).getAsJsonObject();

                        Long id = obj.get("Id").getAsLong();
                        String mesa = obj.get("Mesa").getAsString();
                        String atendente = obj.get("Atendente").getAsString();
                        double txServico = obj.get("TxServico").getAsDouble();
                        double couver = obj.get("Couver").getAsDouble();
                        int idStatus = obj.get("IdStatus").getAsInt();
                        JsonArray arrayProdutos = obj.getAsJsonArray("Produtos");
                        //int idEmpresa = obj.get("IdEmpresa").getAsInt();
                        String data = obj.get("Data").getAsString();

                        Pedido pedido = new Pedido();
                        pedido.setId(id);
                        pedido.setMesa(mesa);
                        pedido.setAtendente(atendente);
                        pedido.setTxServico(txServico);
                        pedido.setCouver(couver);
                        pedido.setIdStatus(idStatus);
                        //pedido.setIdEmpresa(idEmpresa);
                        pedido.setData(data);

                        Produto produto = null;
                        double totalPedidos = 0;
                        for(int i=0; i<arrayProdutos.size(); i++) {
                            produto = new Produto();
                            produto.setId(arrayProdutos.get(i).getAsJsonObject().get("Id").getAsLong());
                            produto.setDescricao(arrayProdutos.get(i).getAsJsonObject().get("Descricao").getAsString());
                            produto.setPreco(arrayProdutos.get(i).getAsJsonObject().get("Preco").getAsDouble());
                            produto.setQuantidade(arrayProdutos.get(i).getAsJsonObject().get("Quantidade").getAsInt());

                            totalPedidos += produto.getTotal();

                            produtos.add(produto);
                        }

                        ArrayAdapter<Produto> adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, produtos);
                        listViewProdutos.setAdapter(adapter);


                        textMesa.setText("Mesa: " + mesa);
                        textGarcom.setText("Atendente Responsável: " + atendente);
                        textCouver.setText("Couver: " + String.valueOf(couver) + "%");
                        textTxServico.setText("Tx. Serv: " + String.valueOf(txServico) + "%");
                        textTotal.setText("Total: R$ " + totalPedidos * ( 1+(couver/100) ) );

                        intentCouver = couver;
                        intentTaxa = txServico;
                        intentValorTotal = totalPedidos * ( 1+(couver/100) );
                        intentGarcom = atendente;
                        intentMesa = mesa;
                        intentIdPedido = id;

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

}
