package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;

import java.util.List;

/**
 * DTO class for SingleTertiaryModelOutput
 */
public class SingleTertiaryModelOutput<T extends ImageInformationOutput> {
    private final Integer modelNumber;
    private final Output2D<T> output2D;
    private final List<String> messages;
    private final List<Object> canonicalInteractions;
    private final List<Object> nonCanonicalInteractions;
    private final List<Object> interStrandInteractions;
    private final List<Object> stackingInteractions;
    private final List<Object> basePhosphateInteractions;
    private final List<Object> baseRiboseInteractions;

    private SingleTertiaryModelOutput(
            Integer modelNumber,
            Output2D<T> output2D,
            List<String> messages,
            List<Object> canonicalInteractions,
            List<Object> nonCanonicalInteractions,
            List<Object> interStrandInteractions,
            List<Object> stackingInteractions,
            List<Object> basePhosphateInteractions,
            List<Object> baseRiboseInteractions) {
        this.modelNumber = modelNumber;
        this.output2D = output2D;
        this.messages = messages;
        this.canonicalInteractions = canonicalInteractions;
        this.nonCanonicalInteractions = nonCanonicalInteractions;
        this.interStrandInteractions = interStrandInteractions;
        this.stackingInteractions = stackingInteractions;
        this.basePhosphateInteractions = basePhosphateInteractions;
        this.baseRiboseInteractions = baseRiboseInteractions;
    }

    public static SingleTertiaryModelOutput<ImageInformationPath> of(
            SingleTertiaryModelOutput<ImageInformationByteArray> engineModelResponse,
            Output2D<ImageInformationPath> output2D
    ) {
        return new SingleTertiaryModelOutput.Builder<ImageInformationPath>()
                .setModelNumber(engineModelResponse.getModelNumber())
                .setOutput2D(output2D)
                .setMessages(engineModelResponse.getMessages())
                .setCanonicalInteractions(engineModelResponse.getCanonicalInteractions())
                .setNonCanonicalInteractions(engineModelResponse.getNonCanonicalInteractions())
                .setInterStrandInteractions(engineModelResponse.getInterStrandInteractions())
                .setStackingInteractions(engineModelResponse.getStackingInteractions())
                .setBasePhosphateInteractions(engineModelResponse.getBasePhosphateInteractions())
                .setBaseRiboseInteractions(engineModelResponse.getBaseRiboseInteractions())
                .build();
    }

    public Output2D<T> getOutput2D() {
        return output2D;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<Object> getCanonicalInteractions() {
        return canonicalInteractions;
    }

    public List<Object> getNonCanonicalInteractions() {
        return nonCanonicalInteractions;
    }

    public List<Object> getInterStrandInteractions() {
        return interStrandInteractions;
    }

    public List<Object> getStackingInteractions() {
        return stackingInteractions;
    }

    public List<Object> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public List<Object> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public Integer getModelNumber() {
        return modelNumber;
    }

    public static class Builder<T extends ImageInformationOutput> {
        private Integer modelNumber;
        private Output2D<T> output2D;
        private List<String> messages;
        private List<Object> canonicalInteractions;
        private List<Object> nonCanonicalInteractions;
        private List<Object> interStrandInteractions;
        private List<Object> stackingInteractions;
        private List<Object> basePhosphateInteractions;
        private List<Object> baseRiboseInteractions;

        public Builder<T> setModelNumber(Integer modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        public Builder<T> setOutput2D(Output2D<T> output2D) {
            this.output2D = output2D;
            return this;
        }

        public Builder<T> setMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public Builder<T> setCanonicalInteractions(List<Object> canonicalInteractions) {
            this.canonicalInteractions = canonicalInteractions;
            return this;
        }

        public Builder<T> setNonCanonicalInteractions(List<Object> nonCanonicalInteractions) {
            this.nonCanonicalInteractions = nonCanonicalInteractions;
            return this;
        }

        public Builder<T> setInterStrandInteractions(List<Object> interStrandInteractions) {
            this.interStrandInteractions = interStrandInteractions;
            return this;
        }

        public Builder<T> setStackingInteractions(List<Object> stackingInteractions) {
            this.stackingInteractions = stackingInteractions;
            return this;
        }

        public Builder<T> setBasePhosphateInteractions(List<Object> basePhosphateInteractions) {
            this.basePhosphateInteractions = basePhosphateInteractions;
            return this;
        }

        public Builder<T> setBaseRiboseInteractions(List<Object> baseRiboseInteractions) {
            this.baseRiboseInteractions = baseRiboseInteractions;
            return this;
        }

        public SingleTertiaryModelOutput<T> build() {
            return new SingleTertiaryModelOutput<>(
                    modelNumber,
                    output2D,
                    messages,
                    canonicalInteractions,
                    nonCanonicalInteractions,
                    interStrandInteractions,
                    stackingInteractions,
                    basePhosphateInteractions,
                    baseRiboseInteractions);
        }
    }
}
