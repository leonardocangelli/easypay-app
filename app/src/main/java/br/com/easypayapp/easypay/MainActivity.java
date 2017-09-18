package br.com.easypayapp.easypay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.cartao.CadastroCartaoActivity;
import br.com.easypayapp.easypay.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkToken();
        setContentView(R.layout.activity_main);
    }

    public void abrirMeusDados(View view) {
        startActivity(new Intent(this, CadastroCartaoActivity.class));
    }

    private void checkToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString(Constants.TOKEN, null);

        if (token == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
