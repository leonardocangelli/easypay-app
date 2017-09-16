package br.com.easypayapp.easypay.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == this.btnLogin) {
            // TODO: Configurar o serviço de Login e guardar os dados no Shared Preferences
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
}