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
import android.widget.ListView;
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

public class MesasAbertasActivity extends ComposeActivity {

    private Context mContext;
    private ListView listMesas;
    private ArrayAdapter<Pedido> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_mesas);
        setBackButton(true);
        setTitleMenu("Mesas");
        mContext = getApplicationContext();
        initViews();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String token = preferences.getString(Constants.TOKEN, null);
        String id = preferences.getString(Constants.ID, null);
        String idEmpresa = preferences.getString(Constants.ID_EMPRESA, null);

        getMesasAbertas(token, id, idEmpresa);
    }

    private void initViews() {
        listMesas = (ListView) findViewById(R.id.listMesas);
    }

    private AdapterView.OnItemClickListener listenerListaMesas = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Pedido pedido = (Pedido)parent.getAdapter().getItem(position);
            Intent intent = new Intent(mContext, DetalhesMesaActivity.class);
            intent.putExtra("idPedido",  String.valueOf(pedido.getId()));
            intent.putExtra("mesa",  pedido.getMesa());
            startActivity(intent);
        }
    };

    public void getMesasAbertas(final String token, final String idGarcom, final String idEmpresa) {

        final List<Pedido> listaMesas = new ArrayList<>();

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(mContext.getString(R.string.carregando));
        pDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.ENDPOINT + "Pedido/PedidosAbertos?idEmpresa=" + idEmpresa,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.hide();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                             for (int i = 0; i < jsonArray.length(); i++) {
                                 JSONObject mesas = jsonArray.getJSONObject(i);

                                 Pedido pedido = new Pedido();
                                 pedido.setId(mesas.getLong("Id"));
                                 pedido.setMesa(mesas.getString("Mesa"));
                                 pedido.setAtendente(mesas.getString("Atendente"));
                                 pedido.setTxServico(mesas.getDouble("TxServico"));
                                 pedido.setCouver(mesas.getDouble("Couver"));
                                 pedido.setIdStatus(mesas.getInt("IdStatus"));
                                 pedido.setIdEmpresa(mesas.getInt("IdEmpresa"));

                                 listaMesas.add(pedido);
                             }
                            adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, listaMesas);
                            listMesas.setAdapter(adapter);
                            listMesas.setOnItemClickListener(listenerListaMesas);
                         } catch (JSONException e) {
                                e.printStackTrace();
                         }
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
