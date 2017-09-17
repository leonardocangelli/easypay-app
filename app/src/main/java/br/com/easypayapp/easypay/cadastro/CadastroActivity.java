package br.com.easypayapp.easypay.cadastro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.R;

public class CadastroActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        initViews();
    }

    private void initViews() {
    }

    public void cadastrar(View view) {
        // TODO: Cadastrar usuario
    }
}
