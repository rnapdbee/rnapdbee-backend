package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

public class TertiaryToDotBracketResultEntity {
    private TertiaryToDotBracketParamsEntity params;
    private String output;

    public TertiaryToDotBracketResultEntity(
            TertiaryToDotBracketParamsEntity params,
            String output) {
        this.params = params;
        this.output = output;
    }
}
