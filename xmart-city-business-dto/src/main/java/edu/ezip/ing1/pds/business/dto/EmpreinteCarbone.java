package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "empreinte_carbone")
public class EmpreinteCarbone {
    private int idEmpreinte;
    private int empreinteKgCO2;
    private String typeDeTransport;
    private double facteurEmission;
    private double distance;

    public EmpreinteCarbone() {
    }

    public EmpreinteCarbone(int idEmpreinte, int empreinteKgCO2, String typeDeTransport, double facteurEmission, double distance) {
        this.idEmpreinte = idEmpreinte;
        this.empreinteKgCO2 = empreinteKgCO2;
        this.typeDeTransport = typeDeTransport;
        this.facteurEmission = facteurEmission;
        this.distance = distance;
    }

    public EmpreinteCarbone(int empreinteKgCO2, String typeDeTransport, double facteurEmission, double distance) {
        this(0, empreinteKgCO2, typeDeTransport, facteurEmission, distance); // ID par dÃ©faut = 0
    }

    public final EmpreinteCarbone build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idEmpreinte", "empreinteKgCO2", "typeDeTransport", "facteurEmission", "distance");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, empreinteKgCO2, typeDeTransport, facteurEmission, distance);
    }

    public int getIdEmpreinte() {
        return idEmpreinte;
    }

    public int getEmpreinteKgCO2() {
        return empreinteKgCO2;
    }

    public String getTypeDeTransport() {
        return typeDeTransport;
    }

    public double getFacteurEmission() {
        return facteurEmission;
    }

    public double getDistance() {
        return distance;
    }

    @JsonProperty("id_empreinte")
    public void setIdEmpreinte(int idEmpreinte) {
        this.idEmpreinte = idEmpreinte;
    }

    @JsonProperty("empreinte_kgCO2")
    public void setEmpreinteKgCO2(int empreinteKgCO2) {
        this.empreinteKgCO2 = empreinteKgCO2;
    }

    @JsonProperty("type_de_transport")
    public void setTypeDeTransport(String typeDeTransport) {
        this.typeDeTransport = typeDeTransport;
    }

    @JsonProperty("facteur_emission")
    public void setFacteurEmission(double facteurEmission) {
        this.facteurEmission = facteurEmission;
    }

    @JsonProperty("distance")
    public void setDistance(double distance) {
        this.distance = distance;
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
        for (final Object value : fieldValues) {
            preparedStatement.setObject(++ix, value);
        }
        return preparedStatement;
    }

    @Override
    public String toString() {
        return "EmpreinteCarbone{" +
                "idEmpreinte=" + idEmpreinte +
                ", empreinteKgCO2=" + empreinteKgCO2 +
                ", typeDeTransport='" + typeDeTransport + '\'' +
                ", facteurEmission=" + facteurEmission +
                ", distance=" + distance +
                '}';
    }
}