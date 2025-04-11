package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "hebergement")
public class Hebergement {
    private int idHebergement;
    private int prixNuit;
    private String nomH;
    private String type;

    public Hebergement() {
    }

    public Hebergement(int idHebergement, int prixNuit, String nomH, String type) {
        this.idHebergement = idHebergement;
        this.prixNuit = prixNuit;
        this.nomH = nomH;
        this.type = type;
    }

    public final Hebergement build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idHebergement", "prixNuit", "nomH", "type");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement,
                String.valueOf(idHebergement),
                String.valueOf(prixNuit),
                nomH,
                type);
    }

    public int getIdHebergement() {
        return idHebergement;
    }

    public int getPrixNuit() {
        return prixNuit;
    }

    public String getNomH() {
        return nomH;
    }

    public String getType() {
        return type;
    }

    @JsonProperty("id_hebergement")
    public void setIdHebergement(int idHebergement) {
        this.idHebergement = idHebergement;
    }

    @JsonProperty("prix_nuit")
    public void setPrixNuit(int prixNuit) {
        this.prixNuit = prixNuit;
    }

    @JsonProperty("nom_h")
    public void setNomH(String nomH) {
        this.nomH = nomH;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    private void setFieldsFromResultSet(final ResultSet resultSet, final String... fieldNames)
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        for (final String fieldName : fieldNames) {
            final Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
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
        return "Hebergement{" +
                "idHebergement=" + idHebergement +
                ", prixNuit=" + prixNuit +
                ", nomH='" + nomH + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
