package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing base triple returned to the REST consumer.
 */
public class BaseTriple {
    @JsonProperty("residue")
    NamedResidue residue;

    @JsonProperty("type")
    String type;

    @JsonProperty("firstPartner")
    NamedResidue firstPartner;

    @JsonProperty("secondPartner")
    NamedResidue secondPartner;

    public NamedResidue getResidue() {
        return residue;
    }

    public String getType() {
        return type;
    }

    public NamedResidue getFirstPartner() {
        return firstPartner;
    }

    public NamedResidue getSecondPartner() {
        return secondPartner;
    }
}
