package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName(value = "hebergement")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hebergement {
    private int idHebergement;
    private int prixNuit;
    private String nomH;
    private String type;
    private String image;
    private Float emission_par_nuit;

    public Hebergement() {}

    public Hebergement(int idHebergement, int prixNuit, String nomH, String type, String image , Float emission_par_nuit) {
        this.idHebergement = idHebergement;
        this.prixNuit = prixNuit;
        this.nomH = nomH;
        this.type = type;
        this.image = image;
        this.emission_par_nuit = emission_par_nuit;
    }

    public final Hebergement build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResultSet(resultSet, "idHebergement", "prixNuit", "nomH", "type", "image", "emission_par_nuit");
        return this;
    }

    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement,
                idHebergement,
                prixNuit,
                nomH,
                type,
                image,
                emission_par_nuit);
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

    public String getImage() {
        return image;
    }

    public Float getEmissionParNuit() {return emission_par_nuit;}

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

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("emission_par_nuit")
    public void setEmissionParNuit(Float emission_par_nuit) {this.emission_par_nuit = emission_par_nuit;}

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

    private final PreparedStatement buildPreparedStatement(PreparedStatement preparedStatement, final Object... fieldValues)
            throws SQLException {
        int ix = 0;
        for (final Object fieldValue : fieldValues) {
            if (fieldValue instanceof byte[]) {
                preparedStatement.setBytes(++ix, (byte[]) fieldValue);
            } else {
                preparedStatement.setObject(++ix, fieldValue);
            }
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
                ", image='" + image  + '\'' +
                ", emission_par_nuit='" + emission_par_nuit  + '\'' +
                '}';
    }
}
