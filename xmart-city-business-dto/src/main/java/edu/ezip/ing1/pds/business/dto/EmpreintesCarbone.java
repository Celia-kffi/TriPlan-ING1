package edu.ezip.ing1.pds.business.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashSet;
import java.util.Set;

public class EmpreintesCarbone {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("empreintes_carbone")
    private Set<EmpreinteCarbone> empreintesCarbone = new LinkedHashSet<>();

    public Set<EmpreinteCarbone> getEmpreintesCarbone() {
        return empreintesCarbone;
    }

    public void setEmpreintesCarbone(Set<EmpreinteCarbone> empreintesCarbone) {
        this.empreintesCarbone = empreintesCarbone;
    }

    public final EmpreintesCarbone add(final EmpreinteCarbone empreinteCarbone) {
        empreintesCarbone.add(empreinteCarbone);
        return this;
    }

    @Override
    public String toString() {
        return "EmpreintesCarbone{" +
                "empreintesCarbone=" + empreintesCarbone +
                '}';
    }
}
