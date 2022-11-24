package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

public class ResultEntity<T, O> {

    private final T params;

    private final O output;

    private ResultEntity(
            T params,
            O output) {
        this.params = params;
        this.output = output;
    }

    public T getParams() {
        return params;
    }

    public O getOutput() {
        return output;
    }

    public static class Builder<T, O> {
        private T params;
        private O output;

        public Builder<T, O> withParams(T params) {
            this.params = params;
            return this;
        }

        public Builder<T, O> withOutput(O output) {
            this.output = output;
            return this;
        }

        public ResultEntity<T, O> build() {
            return new ResultEntity<>(
                    this.getParams(),
                    this.getOutput()
            );
        }

        public T getParams() {
            return params;
        }

        public O getOutput() {
            return output;
        }
    }
}
