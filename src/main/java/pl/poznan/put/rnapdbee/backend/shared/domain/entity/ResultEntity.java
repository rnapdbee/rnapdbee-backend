package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryParams;

import javax.persistence.Id;
import java.util.UUID;

/**
 * Class representing result of one analysis.
 *
 * @param <T> analysis parameters
 * @param <O> analysis results, output from engine service
 */
public class ResultEntity<T, O> {
    @Id
    @JsonIgnore
    private UUID id;
    private T params;
    private O output;

    public ResultEntity() {
    }

    private ResultEntity(
            T params,
            O output) {
        this.id = IdSupplier.generateId();
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

    public static ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> of(
            TertiaryToDotBracketParams tertiaryToDotBracketParams,
            Output3D<ImageInformationPath> output3D
    ) {
        return new ResultEntity.Builder<TertiaryToDotBracketParams, Output3D<ImageInformationPath>>()
                .withParams(tertiaryToDotBracketParams)
                .withOutput(output3D)
                .build();
    }

    public static ResultEntity<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> of(
            TertiaryToMultiSecondaryParams tertiaryToMultiSecondaryParams,
            OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti
    ) {
        return new ResultEntity.Builder<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>>()
                .withParams(tertiaryToMultiSecondaryParams)
                .withOutput(outputMulti)
                .build();
    }

    public UUID getId() {
        return id;
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
