package br.com.easypayapp.easypay.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by joseleonardocangelli on 28/09/17.
 */

public class Pedido {

    private Long id;
    private int idStatus, idEmpresa;
    private String mesa, atendente;
    private double txServico, couver;
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getAtendente() {
        return atendente;
    }

    public void setAtendente(String atendente) {
        this.atendente = atendente;
    }

    public double getTxServico() {
        return txServico;
    }

    public void setTxServico(double txServico) {
        this.txServico = txServico;
    }

    public double getCouver() {
        return couver;
    }

    public void setCouver(double couver) {
        this.couver = couver;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
