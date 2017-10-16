package br.com.easypayapp.easypay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.easypayapp.easypay.R;
import br.com.easypayapp.easypay.model.Produto;

/**
 * Created by joseleonardocangelli on 15/10/17.
 */

public class ListaAdapter extends ArrayAdapter<Produto> {

    private ArrayList<Produto> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView textDescricao, textUnidade, textQuantidade, textTotal;
    }

    public ListaAdapter(ArrayList<Produto> data, Context context) {
        super(context, R.layout.item_lista, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Produto dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lista, parent, false);
            viewHolder.textDescricao = (TextView) convertView.findViewById(R.id.textDescricao);
            viewHolder.textUnidade = (TextView) convertView.findViewById(R.id.textUnidade);
            viewHolder.textQuantidade = (TextView) convertView.findViewById(R.id.textQuantidade);
            viewHolder.textTotal = (TextView) convertView.findViewById(R.id.textTotal);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.textDescricao.setText(dataModel.getDescricao());
        viewHolder.textUnidade.setText(String.valueOf( String.format("%.2f", dataModel.getPreco()) ));
        viewHolder.textQuantidade.setText(String.valueOf(dataModel.getQuantidade()));
        viewHolder.textTotal.setText(String.valueOf( String.format("%.2f", dataModel.getTotal()) ) );

        return convertView;
    }
}
