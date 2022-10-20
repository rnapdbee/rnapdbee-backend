package pl.poznan.put.rnapdbee.backend.shared.domain;

public class ResultEntity<T> {
    private T params;
    private String output;

    public ResultEntity(T params, String output) {
        this.params = params;
        this.output = output;
    }
}
