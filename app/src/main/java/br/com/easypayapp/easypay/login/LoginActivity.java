package br.com.easypayapp.easypay.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.cadastro.CadastroActivity;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;

public class LoginActivity extends BaseActivity {

    private EditText edit_text_email, edit_text_password;
    private VolleyHelperRequest volleyHelperRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        edit_text_email = (EditText) findViewById(R.id.edit_text_email);
        edit_text_password = (EditText) findViewById(R.id.edit_text_password);
        volleyHelperRequest = new VolleyHelperRequest(this);
    }

    public void logar(View view) {
        String email = edit_text_email.getText().toString();
        String senha = edit_text_password.getText().toString();
        Toast.makeText(this, volleyHelperRequest.volleyLogar(email, senha), Toast.LENGTH_LONG).show();
    }

    public void abrirCadastro(View view) {
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }

}