package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

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

    public final String toString() {
        String chain = StringUtils.isBlank(this.chainIdentifier) ? "" : (this.chainIdentifier + '.');
        String icode = StringUtils.isBlank(this.insertionCode) ? "" : this.insertionCode;
        String name = (this.oneLetterName == ' ') ? "" : Character.toString(this.oneLetterName);
        return chain + name + this.residueNumber + icode;
    }
}
