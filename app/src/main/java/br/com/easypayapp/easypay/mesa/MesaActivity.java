package br.com.easypayapp.easypay.mesa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.R;

public class MesaActivity extends ComposeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa);
        setTitleMenu("Minha Mesa");
        setBackButton(true);
    }

    public void fecharConta(View view) {
        Toast.makeText(this, "Fechar Conta!", Toast.LENGTH_SHORT).show();
    }
}
