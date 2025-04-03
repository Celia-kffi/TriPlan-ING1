package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@JsonRootName(value = "voyage")
public class Voyage {
    private int idVoyage;
    private double montant;
    private String typeVoyage;
    private String dateDepart;
    private String dateRetour;
    private int idEmpreinte;
    private int idClient;

    public Voyage() {
    }
    public Voyage(int idVoyage, double montant, String typeVoyage, String dateDepart, String dateRetour, int idEmpreinte, int idClient) {
        this.idVoyage = idVoyage;
        this.montant = montant;
        this.typeVoyage = typeVoyage;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.idEmpreinte = idEmpreinte;
        this.idClient = idClient;
    }


    public Voyage(double montant, String typeVoyage, String dateDepart, String dateRetour, int idEmpreinte, int idClient) {
        this.montant = montant;
        this.typeVoyage = typeVoyage;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.idEmpreinte = idEmpreinte;
        this.idClient = idClient;
    }

    public final Voyage build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idVoyage", "montant", "typeVoyage", "dateDepart", "dateRetour", "idEmpreinte", "idClient");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, montant, typeVoyage, dateDepart, dateRetour, idEmpreinte, idClient);
    }

    public int getIdVoyage() { return idVoyage; }
    public double getMontant() { return montant; }
    public String getTypeVoyage() { return typeVoyage; }
    public String getDateDepart() { return dateDepart; }
    public String getDateRetour() { return dateRetour; }
    public int getIdEmpreinte() { return idEmpreinte; }
    public int getIdClient() { return idClient; }

    @JsonProperty("voyage_id")
    public void setIdVoyage(int idVoyage) { this.idVoyage = idVoyage; }

    @JsonProperty("voyage_montant")
    public void setMontant(double montant) { this.montant = montant; }

    @JsonProperty("voyage_type")
    public void setTypeVoyage(String typeVoyage) { this.typeVoyage = typeVoyage; }

    @JsonProperty("voyage_date_depart")
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }

    @JsonProperty("voyage_date_retour")
    public void setDateRetour(String dateRetour) { this.dateRetour = dateRetour; }

    @JsonProperty("voyage_id_empreinte")
    public void setIdEmpreinte(int idEmpreinte) { this.idEmpreinte = idEmpreinte; }

    @JsonProperty("voyage_id_client")
    public void setIdClient(int idClient) { this.idClient = idClient; }

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
        return "Voyage{" +
                "idVoyage=" + idVoyage +
                ", montant=" + montant +
                ", typeVoyage='" + typeVoyage + '\'' +
                ", dateDepart='" + dateDepart + '\'' +
                ", dateRetour='" + dateRetour + '\'' +
                ", idEmpreinte=" + idEmpreinte +
                ", idClient=" + idClient +
                '}';
    }
}
