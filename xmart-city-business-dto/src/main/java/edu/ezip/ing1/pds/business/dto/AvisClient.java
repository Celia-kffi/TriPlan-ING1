package edu.ezip.ing1.pds.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("avis_client")
public class AvisClient {

    private int idAvis;
    private int note;
    private String dateAvis;
    private String commentaires;
    private int idClient;

    private String nomClient;
    private String prenomClient;
    private int ageClient;

    public AvisClient() {
    }

    public AvisClient(int idAvis, int note, String dateAvis, String commentaires, int idClient) {
        this.idAvis = idAvis;
        this.note = note;
        this.dateAvis = dateAvis;
        this.commentaires = commentaires;
        this.idClient = idClient;
    }

    public int getIdAvis() { return idAvis; }
    public int getNote() { return note; }
    public String getDateAvis() { return dateAvis; }
    public String getCommentaires() { return commentaires; }
    public int getIdClient() { return idClient; }
    public String getNomClient() { return nomClient; }
    public String getPrenomClient() { return prenomClient; }
    public int getAgeClient() { return ageClient; }

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

    @JsonProperty("nom_client")
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    @JsonProperty("prenom_client")
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }

    @JsonProperty("age_client")
    public void setAgeClient(int ageClient) { this.ageClient = ageClient; }

    @Override
    public String toString() {
        return "AvisClient{" +
                "idAvis=" + idAvis +
                ", note=" + note +
                ", dateAvis='" + dateAvis + '\'' +
                ", commentaires='" + commentaires + '\'' +
                ", idClient=" + idClient +
                ", nomClient='" + nomClient + '\'' +
                ", prenomClient='" + prenomClient + '\'' +
                ", ageClient=" + ageClient +
                '}';
    }
}
