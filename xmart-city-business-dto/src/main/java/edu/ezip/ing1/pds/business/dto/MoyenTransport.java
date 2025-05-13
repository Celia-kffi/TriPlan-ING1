package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "moyen_transport")
public class MoyenTransport {
    private String idMoyenDestination;
    private String typeTransports;
    private double facteurEmission;

    public MoyenTransport() {
    }

    public MoyenTransport(String idMoyenDestination, String typeTransports, double facteurEmission) {
        this.idMoyenDestination = idMoyenDestination;
        this.typeTransports = typeTransports;
        this.facteurEmission = facteurEmission;
    }

    public final MoyenTransport build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idMoyenDestination", "typeTransports", "facteurEmission");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, idMoyenDestination, typeTransports, String.valueOf(facteurEmission));
    }

    public String getIdMoyenDestination() {
        return idMoyenDestination;
    }

    public String getTypeTransports() {
        return typeTransports;
    }

    public double getFacteurEmission() {
        return facteurEmission;
    }

    @JsonProperty("id_moyen_destination")
    public void setIdMoyenDestination(String idMoyenDestination) {
        this.idMoyenDestination = idMoyenDestination;
    }

    @JsonProperty("type_transports")
    public void setTypeTransports(String typeTransports) {
        this.typeTransports = typeTransports;
    }

    @JsonProperty("facteur_emission")
    public void setFacteurEmission(double facteurEmission) {
        this.facteurEmission = facteurEmission;
    }

    private void setFieldsFromResultSet(final ResultSet resultSet, final String... fieldNames)
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        for (final String fieldName : fieldNames) {
            final Field field = this.getClass().getDeclaredField(fieldName);
            if (field.getType() == double.class) {
                field.set(this, resultSet.getDouble(fieldName));
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
        return "MoyenTransport{" +
                "idMoyenDestination='" + idMoyenDestination + '\'' +
                ", typeTransports='" + typeTransports + '\'' +
                ", facteurEmission=" + facteurEmission +
                '}';
    }
}
