package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing base pair.
 */
public class BasePair {
    @JsonProperty("interactionType")
    String interactionType;
    @JsonProperty("saenger")
    String saenger;
    @JsonProperty("leontisWesthof")
    String leontisWesthof;
    @JsonProperty("bPh")
    String bPh;
    @JsonProperty("br")
    String br;
    @JsonProperty("stackingTopology")
    String stackingTopology;
    @JsonProperty("leftResidue")
    NamedResidue leftResidue;
    @JsonProperty("rightResidue")
    NamedResidue rightResidue;

    public String getInteractionType() {
        return interactionType;
    }

    public String getSaenger() {
        return saenger;
    }

    public String getLeontisWesthof() {
        return leontisWesthof;
    }

    public String getbPh() {
        return bPh;
    }

    public String getBr() {
        return br;
    }

    public String getStackingTopology() {
        return stackingTopology;
    }

    public NamedResidue getLeftResidue() {
        return leftResidue;
    }

    public NamedResidue getRightResidue() {
        return rightResidue;
    }
}
