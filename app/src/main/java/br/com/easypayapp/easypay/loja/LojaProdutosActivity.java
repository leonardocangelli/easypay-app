package br.com.easypayapp.easypay.loja;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.R;

public class LojaProdutosActivity extends ComposeActivity {

    private Context mContext;
    private ListView listViewProdutos;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loja_produtos);
        mContext = getApplicationContext();
        setTitle("Produtos");
        setBackButton(true);

        initViews();
    }

    private void initViews() {
        listViewProdutos = (ListView) findViewById(R.id.listViewProdutos);
        String produtos[] = {
                             "Banana (2) R$ 7,00",
                             "Pão (1) R$ 5,00",
                             "Presunto (1) R$ 4,00",
                             "Queijo (3) R$ 10,00",
                             "Pastel (2) R$ 12,00",
                             "Bolo (1) R$ 7,00",
                             "Cookie (1) R$ 14,00"
                            };
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, produtos);
        listViewProdutos.setAdapter(adapter);
    }

    public void pagar(View view) {
        Intent intent = new Intent(mContext, ConfirmacaoPagamento.class);
        startActivity(intent);
    }
}
