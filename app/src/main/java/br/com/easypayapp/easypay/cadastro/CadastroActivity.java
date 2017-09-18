package br.com.easypayapp.easypay.cadastro;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.BaseActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.helpers.VolleyHelperRequest;
import br.com.easypayapp.easypay.login.LoginActivity;
import br.com.easypayapp.easypay.model.Usuario;

public class CadastroActivity extends BaseActivity {

    private EditText edit_text_nome,
                    edit_text_email,
                    edit_text_telefone,
                    edit_text_cpf,
                    edit_text_senha;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        mContext = getApplicationContext();
        initViews();
    }

    private void initViews() {
        edit_text_nome = (EditText) findViewById(R.id.edit_text_nome);
        edit_text_email = (EditText) findViewById(R.id.edit_text_email);
        edit_text_telefone = (EditText) findViewById(R.id.edit_text_fone);
        edit_text_cpf = (EditText) findViewById(R.id.edit_text_cpf);
        edit_text_senha = (EditText) findViewById(R.id.edit_text_password);
    }

    public void cadastrar(View view) {

        String nome = edit_text_nome.getText().toString();
        String email = edit_text_email.getText().toString();
        String telefone = edit_text_telefone.getText().toString();
        String cpf = edit_text_cpf.getText().toString();
        String senha = edit_text_senha.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setSenha(senha);

        doRequestCadastro(usuario);
    }

    private void doRequestCadastro(final Usuario usuario) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.ENDPOINT + "usuario",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CadastroActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("Nome", usuario.getNome());
                params.put("Email", usuario.getEmail());
                params.put("Telefone", usuario.getTelefone());
                params.put("Cpf", usuario.getCpf());
                params.put("Senha", usuario.getSenha());
                return params;
            }
        };

        VolleyHelperRequest.getInstance(mContext).addToRequestQueue(stringRequest);
    }
}
