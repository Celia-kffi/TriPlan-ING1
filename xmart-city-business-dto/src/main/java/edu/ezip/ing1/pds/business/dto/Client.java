package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "client")
public class Client {
    private String nom;
    private String prenom;
    private int age;
    private String nationalite;
    private double budget;
    private int idClient;

    public Client() {
    }

    public Client(String nom, String prenom, int age, String nationalite, double budget) {
        this.nom = nom;
        this.prenom = prenom;
        this.age = age;
        this.nationalite = nationalite;
        this.budget = budget;
    }

    public final Client build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "nom", "prenom", "age", "nationalite", "budget", "idClient");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, nom, prenom, age, nationalite, budget);
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


    @JsonProperty("client_nom")
    public void setNom(String nom) {
        this.nom = nom;
    }

    @JsonProperty("client_prenom")
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @JsonProperty("client_age")
    public void setAge(int age) {
        this.age = age;
    }

    @JsonProperty("client_nationalite")
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    @JsonProperty("client_budget")
    public void setBudget(double budget) {
        this.budget = budget;
    }

    @JsonProperty("client_id")
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    private void setFieldsFromResultSet(final ResultSet resultSet, final String... fieldNames)
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        for (final String fieldName : fieldNames) {
            final Field field = this.getClass().getDeclaredField(fieldName);
            field.set(this, resultSet.getObject(fieldName));
        }
    }

    private final PreparedStatement buildPreparedStatement(PreparedStatement preparedStatement, final Object... fieldValues)
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
                '}';
    }
}