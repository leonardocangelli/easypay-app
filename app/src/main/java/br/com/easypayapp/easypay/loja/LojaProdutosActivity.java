package br.com.easypayapp.easypay.loja;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.adapter.ListaAdapter;
import br.com.easypayapp.easypay.model.Produto;

public class LojaProdutosActivity extends ComposeActivity {

    private Context mContext;
    private ListView listViewProdutos;
    private TextView textTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loja_produtos);
        mContext = getApplicationContext();
        setTitle(mContext.getString(R.string.produtos));
        setBackButton(true);

        initViews();
    }

    private void initViews() {
        listViewProdutos = (ListView) findViewById(R.id.listViewProdutos);
        textTotal = (TextView) findViewById(R.id.textTotal);
        ArrayList<Produto> produtos = new ArrayList<>();

        produtos.add(new Produto(null, "Banana", "", 7.00, 14.00, 2));
        produtos.add(new Produto(null, "Pão", "", 5.00, 5.00, 1));
        produtos.add(new Produto(null, "Presunto", "", 4.00, 4.00, 1));
        produtos.add(new Produto(null, "Queijo", "", 10.00, 30.00, 3));
        produtos.add(new Produto(null, "Pastel", "", 12.00, 24.00, 2));
        produtos.add(new Produto(null, "Bolo", "", 7.00, 7.00, 1));
        produtos.add(new Produto(null, "Cookie", "", 14.00, 14.00, 1));

        double total = 0;
        for(Produto p : produtos) total += p.getTotal();
        textTotal.setText("Total: R$ " + String.format("%.2f", total ));

        ListAdapter adapter = new ListaAdapter(produtos, mContext);
        //adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, produtos);
        listViewProdutos.setAdapter(adapter);
    }

    public void pagar(View view) {
        Intent intent = new Intent(mContext, ConfirmacaoPagamento.class);
        startActivity(intent);
    }
}
