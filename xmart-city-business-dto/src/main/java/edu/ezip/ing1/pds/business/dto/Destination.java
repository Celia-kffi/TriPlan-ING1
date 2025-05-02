package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "destination")
public class Destination {
    private String idDestination;
    private String pays;
    private String ville;
    private String climat;
    private double prixParJour;


    public Destination() {
    }

    public Destination(String idDestination, String pays, String ville, String climat, Double prixParJour) {
        this.idDestination = idDestination;
        this.pays = pays;
        this.ville = ville;
        this.climat = climat;
        this.prixParJour = 0.0;
    }

    public Destination(String pays, String ville, String climat, double prixParJour) {
        this.pays = pays;
        this.ville = ville;
        this.climat = climat;
        this.prixParJour = 0.0;
    }


    public final Destination build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idDestination", "pays", "ville", "climat","prix_par_jour");
        return this;
    }


    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, pays, ville, climat);
    }


    public String getIdDestination() {
        return idDestination;
    }

    public String getPays() {
        return pays;
    }

    public String getVille() {
        return ville;
    }

    public String getClimat() {
        return climat;
    }
    public double getPrixParJour() {
        return prixParJour;

    }


    @JsonProperty("id_destination")
    public void setIdDestination(String idDestination) {
        this.idDestination = idDestination;
    }

    @JsonProperty("pays")
    public void setPays(String pays) {
        this.pays = pays;
    }

    @JsonProperty("ville")
    public void setVille(String ville) {
        this.ville = ville;
    }

    @JsonProperty("climat")
    public void setClimat(String climat) {
        this.climat = climat;
    }
    @JsonProperty("prixParJour")
    public void setPrixParJour(double prixParJour) {
        this.prixParJour = prixParJour;
    }


    private void setFieldsFromResultSet(final ResultSet resultSet, final String... fieldNames)
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
        return "Destination{" +
                "idDestination=" + idDestination +
                ", pays='" + pays + '\'' +
                ", ville='" + ville + '\'' +
                ", climat='" + climat + '\'' +
                ", prixParJour=" + prixParJour +
                '}';
    }
}
