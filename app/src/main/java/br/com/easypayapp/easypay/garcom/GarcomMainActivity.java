package br.com.easypayapp.easypay.garcom;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import br.com.easypayapp.easypay.ComposeActivity;
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.barcode.BarcodeCaptureActivity;

public class GarcomMainActivity extends ComposeActivity {

    private static final String LOG_TAG = GarcomMainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garcom_main);
        setTitleMenu("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;

                    String id = barcode.displayValue;
                    Intent intent = new Intent(this, AberturaMesaActivity.class);
                    intent.putExtra("idCliente", id);
                    startActivity(intent);

                } else Toast.makeText(this, R.string.no_barcode_captured, Toast.LENGTH_SHORT).show();
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    public void openTable(View view) {
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    public void manageTables(View view) {
        startActivity(new Intent(this, MesasAbertasActivity.class));
    }
}
