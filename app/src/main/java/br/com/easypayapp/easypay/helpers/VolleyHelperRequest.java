package br.com.easypayapp.easypay.helpers;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.easypayapp.easypay.Constants;
import br.com.easypayapp.easypay.login.LoginActivity;

/**
 * Created by joseleonardocangelli on 17/09/17.
 */

public class VolleyHelperRequest {

    private RequestQueue requestQueue;
    public String volleyResponse;

    public VolleyHelperRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public String volleyLogar(final String email, final String senha) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.ENDPOINT + "usuario/login", onPostsLoaded, onPostsError) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("Email", email);
                params.put("Senha", senha);

                return params;
            }
        };
        requestQueue.add(request);
        return volleyResponse;
    }

    public String volleyGET(int id) {
        StringRequest request = new StringRequest(Request.Method.GET, Constants.ENDPOINT + "usuario/" + id, onPostsLoaded, onPostsError);
        requestQueue.add(request);
        return volleyResponse;
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            volleyResponse = response;
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            volleyResponse = error.getMessage();
        }
    };
}
