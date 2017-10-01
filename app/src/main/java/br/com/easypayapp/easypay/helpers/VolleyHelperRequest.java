package br.com.easypayapp.easypay.helpers;

import android.app.ProgressDialog;
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
import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.login.LoginActivity;

/**
 * Created by joseleonardocangelli on 17/09/17.
 */

public class VolleyHelperRequest {

    private static VolleyHelperRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleyHelperRequest(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelperRequest getInstance(Context context){
        if(mInstance == null) {
            mInstance = new VolleyHelperRequest(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

}
