package com.projeto.unify.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "universidades")
public class Universidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(name = "data_fundacao", nullable = false)
    private LocalDate fundacao;

    @Column(length = 10)
    private String sigla;

    public Universidade() {}

    public Universidade(String nome, String cnpj, LocalDate fundacao, String sigla) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.fundacao = fundacao;
        this.sigla = sigla;
    }

    // Getters e Setters
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public LocalDate getFundacao() {
        return fundacao;
    }

    public void setFundacao(LocalDate fundacao) {
        this.fundacao = fundacao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
