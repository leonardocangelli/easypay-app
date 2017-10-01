package br.com.easypayapp.easypay.model;

import android.support.annotation.StringDef;

/**
 * Created by joseleonardocangelli on 01/10/17.
 */

public class Produto {

    private Long id;
    private String descricao;
    private double preco, total;
    private int quantidade;

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

    @Override
    public String toString() {
        return descricao + "\t \tR$ " + preco + " \t \t" + quantidade + "\t \tR$ " + getTotal();
    }
}
