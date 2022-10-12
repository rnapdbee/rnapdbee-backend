package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

public class SecondaryToDotBracketResultEntity {
    private SecondaryToDotBracketParamsEntity params;
    private String output;

    public SecondaryToDotBracketResultEntity(
            SecondaryToDotBracketParamsEntity params,
            String output) {
        this.params = params;
        this.output = output;
    }
}
