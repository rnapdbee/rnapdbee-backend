package pl.poznan.put.rnapdbee.backend.shared.domain.entity;

import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.SecondaryToDotBracketService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

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

    public ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> of(
            Output2D<ImageInformationByteArray> engineOutput2DResponse,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String pathToSVGImage
    ) {
        Output2D<ImageInformationPath> output2D =
                new Output2D.Builder<ImageInformationPath>()
                        .withStrands(engineOutput2DResponse.getStrands())
                        .withBpSeq(engineOutput2DResponse.getBpSeq())
                        .withCt(engineOutput2DResponse.getCt())
                        .withInteractions(engineOutput2DResponse.getInteractions())
                        .withStructuralElement(engineOutput2DResponse.getStructuralElements())
                        .withImageInformation(ImageInformationPath.of(engineOutput2DResponse.getImageInformation(), pathToSVGImage))
                        .build();

        return new ResultEntity.Builder<SecondaryToDotBracketParams, Output2D<ImageInformationPath>>()
                .withParams(SecondaryToDotBracketParams.of(
                        removeIsolated,
                        structuralElementsHandling,
                        visualizationTool
                ))
                .withOutput(output2D)
                .build();
    }
}
