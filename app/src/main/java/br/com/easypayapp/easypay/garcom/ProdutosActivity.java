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
import android.widget.ListAdapter;
import android.widget.ListView;
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
import br.com.easypayapp.easypay.adapter.ListaAdapter;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.model.Produto;
import br.com.easypayapp.easypay.model.ProdutoSpinner;

public class ProdutosActivity extends ComposeActivity {

    private Context mContext;
    private ListView listProdutos;
    private ArrayAdapter<Produto> adapter;
    private String idPedido = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_produtos);
        setBackButton(true);
        mContext = getApplicationContext();
        setTitleMenu(mContext.getString(R.string.pedido));
        initViews();

        idPedido = getIntent().getStringExtra("idPedido");
    }

    private void initViews() {
        listProdutos = (ListView) findViewById(R.id.listProdutos);
        ArrayList<Produto> produtos = this.getIntent().getParcelableArrayListExtra("produtos");
        ListAdapter adapter = new ListaAdapter(produtos, mContext);
        //adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, produtos);
        listProdutos.setAdapter(adapter);
    }

    public void finalizar(View view) {
        finishAffinity();
        startActivity(new Intent(mContext, GarcomMainActivity.class));
    }
}
