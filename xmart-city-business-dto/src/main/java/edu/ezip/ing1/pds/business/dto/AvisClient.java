package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.sql.ResultSet;
import java.sql.SQLException;

@JsonRootName("avis_client")
public class AvisClient {

    private int idAvis;
    private int note;
    private String dateAvis;
    private String commentaires;
    private int idClient;

    public AvisClient() {
    }

    public AvisClient(int idAvis, int note, String dateAvis, String commentaires, int idClient) {
        this.idAvis = idAvis;
        this.note = note;
        this.dateAvis = dateAvis;
        this.commentaires = commentaires;
        this.idClient = idClient;
    }

    public final AvisClient build(final ResultSet resultSet) throws SQLException {
        this.idAvis = resultSet.getInt("id_avis");
        this.note = resultSet.getInt("note");
        this.dateAvis = resultSet.getString("date_avis");
        this.commentaires = resultSet.getString("commentaires");
        this.idClient = resultSet.getInt("id_client");
        return this;
    }

    public int getIdAvis() { return idAvis; }
    public int getNote() { return note; }
    public String getDateAvis() { return dateAvis; }
    public String getCommentaires() { return commentaires; }
    public int getIdClient() { return idClient; }

    @JsonProperty("id_avis")
    public void setIdAvis(int idAvis) { this.idAvis = idAvis; }

    @JsonProperty("note")
    public void setNote(int note) { this.note = note; }

    @JsonProperty("date_avis")
    public void setDateAvis(String dateAvis) { this.dateAvis = dateAvis; }

    @JsonProperty("commentaires")
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    @JsonProperty("id_client")
    public void setIdClient(int idClient) { this.idClient = idClient; }

    @Override
    public String toString() {
        return "AvisClient{" +
                "idAvis=" + idAvis +
                ", note=" + note +
                ", dateAvis='" + dateAvis + '\'' +
                ", commentaires='" + commentaires + '\'' +
                ", idClient=" + idClient +
                '}';
    }
}
