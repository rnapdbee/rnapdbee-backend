package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;

public class ResultEntity<T, O> {

    private final T params;

    private final O output;

    private ResultEntity(
            T params,
            O output) {
        this.params = params;
        this.output = output;
    }

    public static ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> of(
            SecondaryToDotBracketParams secondaryToDotBracketParams,
            Output2D<ImageInformationPath> output2D
    ) {
        return new ResultEntity.Builder<SecondaryToDotBracketParams, Output2D<ImageInformationPath>>()
                .withParams(secondaryToDotBracketParams)
                .withOutput(output2D)
                .build();
    }

    public static ResultEntity<TertiaryToDotBracketParams, Output3D> of(
            TertiaryToDotBracketParams tertiaryToDotBracketParams,
            Output3D output3D
    ) {
        return new ResultEntity.Builder<TertiaryToDotBracketParams, Output3D>()
                .withParams(tertiaryToDotBracketParams)
                .withOutput(output3D)
                .build();
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
