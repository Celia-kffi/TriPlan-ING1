package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "client")
public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private int age;
    private String nationalite;
    private double budget;
    private String idPaiement;


    public Client() {
    }
    public Client( int idClient,String nom, String prenom, int age, String nationalite, double budget, String idPaiement) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.nationalite = nationalite;
        this.budget = budget;
        this.idPaiement = idPaiement;
    }

    public Client(String nom, String prenom, int age, String nationalite, double budget, String idPaiement) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.nationalite = nationalite;
        this.budget = budget;
        this.idPaiement = idPaiement;
    }


    public final Client build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResulset(resultSet, "idClient","nom", "prenom", "age", "nationalite", "budget", "idPaiement");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, nom, prenom, String.valueOf(age), nationalite, String.valueOf(budget),idPaiement);
    }





    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getAge() {
        return age;
    }

    public String getNationalite() {
        return nationalite;
    }

    public double getBudget() {
        return budget;
    }

    public int getIdClient() {
        return idClient;
    }
    public String getIdPaiement(){
        return idPaiement;
    }

    @JsonProperty("id")
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @JsonProperty("nom")
    public void setNom(String nom) {
        this.nom = nom;
    }

    @JsonProperty("prenom")
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @JsonProperty("age")
    public void setAge(int age) {
        this.age = age;
    }

    @JsonProperty("nationalite")
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    @JsonProperty("budget")
    public void setBudget(double budget) {
        this.budget = budget;
    }
    @JsonProperty("id_paiement")
    public void setIdPaiement(String idPaiement) {
        this.idPaiement = idPaiement;
    }



    private void setFieldsFromResulset(final ResultSet resultSet, final String... fieldNames)
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        for (final String fieldName : fieldNames) {
            final Field field = this.getClass().getDeclaredField(fieldName);
            if (field.getType() == int.class) {
                field.set(this, resultSet.getInt(fieldName));
            } else {
                field.set(this, resultSet.getObject(fieldName));
            }
        }
    }

    private final PreparedStatement buildPreparedStatement(PreparedStatement preparedStatement, final String... fieldValues)
            throws SQLException {
        int ix = 0;
        for (final Object fieldValue : fieldValues) {
            preparedStatement.setObject(++ix, fieldValue);
        }
        return preparedStatement;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", age=" + age +
                ", nationalite='" + nationalite + '\'' +
                ", budget=" + budget +
                ", idPaiement='" + idPaiement + '\'' +
                '}';
    }
}