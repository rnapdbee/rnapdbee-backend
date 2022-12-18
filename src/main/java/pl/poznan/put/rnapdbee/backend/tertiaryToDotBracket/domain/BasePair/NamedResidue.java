package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class representing named residue.
 */
public class NamedResidue {
    @JsonProperty("chainIdentifier")
    private final String chainIdentifier;
    @JsonProperty("residueNumber")
    private final int residueNumber;
    @JsonProperty("insertionCode")
    private final String insertionCode;
    @JsonProperty("oneLetterName")
    private final char oneLetterName;

    public NamedResidue(
            String chainIdentifier,
            int residueNumber,
            String insertionCode,
            char oneLetterName) {
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
        this.oneLetterName = oneLetterName;
    }
}
