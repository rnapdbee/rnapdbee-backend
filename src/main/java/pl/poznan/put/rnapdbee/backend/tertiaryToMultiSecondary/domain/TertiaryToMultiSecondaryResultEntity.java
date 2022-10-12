package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

public class TertiaryToMultiSecondaryResultEntity {
    private TertiaryToMultiSecondaryParamsEntity params;
    private String output;

    public TertiaryToMultiSecondaryResultEntity(
            TertiaryToMultiSecondaryParamsEntity params,
            String output) {
        this.params = params;
        this.output = output;
    }
}
