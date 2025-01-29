package com.max_doc.dto;

public class DocumentDTO {

    private String titulo;
    private String descricao;
    private Integer versao;
    private String sigla;
    private String fase;

    // Getters e Setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    @Override
    public String toString() {
        return "DocumentDTO{" +
                "titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", versao='" + versao + '\'' +
                ", sigla='" + sigla + '\'' +
                ", fase='" + fase + '\'' +
                '}';
    }
}

