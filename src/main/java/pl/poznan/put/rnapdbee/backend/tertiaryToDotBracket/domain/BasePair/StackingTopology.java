package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * enum for Stacking Topology
 */
public enum StackingTopology {

    @JsonProperty("upward")
    UPWARD,
    @JsonProperty("downward")
    DOWNWARD,
    @JsonProperty("inward")
    INWARD,
    @JsonProperty("outward")
    OUTWARD
}
