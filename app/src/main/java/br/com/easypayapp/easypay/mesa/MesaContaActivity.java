package br.com.easypayapp.easypay.mesa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.MainActivity;
import br.com.easypayapp.easypay.R;

public class MesaContaActivity extends ComposeActivity {

    private Context mContext;
    private CheckBox chkTxServ;
    private TextView textTotal, textCouver, textService, textNumTable, textResponsible;
    private String valorFinal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa_conta);
        mContext = getApplicationContext();
        setTitleMenu("Pagamento");
        setBackButton(true);

        initViews();
    }

    public void initViews() {
        Intent intent = getIntent();
        final Double valorTotal = intent.getExtras().getDouble("total");
        final Double txServico = intent.getExtras().getDouble("taxa");
        final Double couver = intent.getExtras().getDouble("couver");
        final String mesa = intent.getExtras().getString("mesa");
        final String garcom = intent.getExtras().getString("garcom");

        chkTxServ = (CheckBox) findViewById(R.id.chkTxServ);
        textTotal = (TextView) findViewById(R.id.textTotal);
        textCouver = (TextView) findViewById(R.id.textCouver);
        textService = (TextView) findViewById(R.id.textService);
        textNumTable = (TextView) findViewById(R.id.textNumTable);
        textResponsible = (TextView) findViewById(R.id.textResponsible);


        textNumTable.setText("Mesa: " + mesa);
        textResponsible.setText("Atendente Responsável: " + garcom);
        textCouver.setText("Couver: " + String.valueOf(couver) + "%");
        textService.setText("Tx. Serv: " + String.valueOf(txServico) + "%");
        textTotal.setText("Total: R$ " + String.valueOf(valorTotal));

        valorFinal = String.valueOf(valorTotal);

        chkTxServ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chkTxServ.isChecked()) {
                    textTotal.setText("Total: R$ " + String.valueOf( String.format("%.2f", valorTotal * ( 1+(txServico/100) )) ) );
                    valorFinal = String.format("%.2f", valorTotal * ( 1+(txServico/100) ));
                } else {
                    textTotal.setText("Total: R$ " + String.valueOf(valorTotal));
                    valorFinal = String.valueOf(valorTotal);
                }
            }
        });
    }

    public void chamarGarcom(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Deseja chamar o garçom?");
        alert.setMessage("A qualquer momento você pode chamar o garcom caso tenha dúvidas ou queira realizar seu pagamento direto no estabelecimento.");
        alert.setPositiveButton("Chamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MesaContaActivity.this, "O garçom foi chamado!", Toast.LENGTH_LONG).show();
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }

    public void pagarEasyPay(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String n_cartao = preferences.getString(Constants.N_CARTAO, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if (!n_cartao.equals("")) {
            alert.setTitle("Confirmar pagamento");
            alert.setMessage("Você autoriza o pagamento no valor de R$ " + valorFinal + " em seu cartão de crédito de número " + n_cartao + " ?");
            alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finalizarConta();
                }
            });
            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        } else {
            alert.setMessage("Para pagar pelo EasyPay você precisa cadastrar seu cartão de crédito.");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }
        alert.create().show();
    }

    public void finalizarConta() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.MESA_ABERTA, 0);
        editor.commit();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("EasyPay");
        alert.setMessage("Pagamento realizado com sucesso. Agradecemos a preferência!");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                startActivity(new Intent(MesaContaActivity.this, MainActivity.class));
            }
        });
        alert.create().show();
    }

}
