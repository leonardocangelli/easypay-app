package br.com.easypayapp.easypay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joseleonardocangelli on 08/10/17.
 */

public class ProdutoSpinner implements Parcelable {

    private Long id;
    private String descricao, observacao;
    private double preco, total;
    private int quantidade;

    public ProdutoSpinner(String descricao) {
        this.descricao = descricao;
    }

    public ProdutoSpinner() {
    }

    protected ProdutoSpinner(Parcel in) {
        descricao = in.readString();
        observacao = in.readString();
        preco = in.readDouble();
        total = in.readDouble();
        quantidade = in.readInt();
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel in) {
            return new Produto(in);
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getTotal() {
        return quantidade * preco;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(descricao);
        dest.writeString(observacao);
        dest.writeDouble(preco);
        dest.writeDouble(total);
        dest.writeInt(quantidade);
    }

    private void readFromParcel(Parcel in) {
        descricao = in.readString();
        observacao = in.readString();
        preco = in.readDouble();
        total = in.readDouble();
        quantidade = in.readInt();
    }
}
