package br.com.easypayapp.easypay.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by joseleonardocangelli on 18/09/17.
 */

public class Cartao {

    private Long id;
    private String nome, numero, mesVencimento, anoVencimento, cvv;
    private Date dataVencimento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDataVencimento() {
        return "01/" + mesVencimento + "/20" + anoVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getMesVencimento() {
        return mesVencimento;
    }

    public void setMesVencimento(String mesVencimento) {
        this.mesVencimento = mesVencimento;
    }

    public String getAnoVencimento() {
        return anoVencimento;
    }

    public void setAnoVencimento(String anoVencimento) {
        this.anoVencimento = anoVencimento;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
