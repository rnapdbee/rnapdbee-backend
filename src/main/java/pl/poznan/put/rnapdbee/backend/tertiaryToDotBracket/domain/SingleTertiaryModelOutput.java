package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformation;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;

import java.util.List;

/**
 * Class for SingleTertiaryModelOutput
 */
public class SingleTertiaryModelOutput<T extends ImageInformation> {
    private final Integer modelNumber;
    private final Output2D<T> output2D;
    private final List<String> messages;
    private final List<BasePair> canonicalInteractions;
    private final List<BasePair> nonCanonicalInteractions;
    private final List<BasePair> interStrandInteractions;
    private final List<BasePair> stackingInteractions;
    private final List<BasePair> basePhosphateInteractions;
    private final List<BasePair> baseRiboseInteractions;
    private final List<BaseTriple> baseTriples;

    private SingleTertiaryModelOutput(
            Integer modelNumber,
            Output2D<T> output2D,
            List<String> messages,
            List<BasePair> canonicalInteractions,
            List<BasePair> nonCanonicalInteractions,
            List<BasePair> interStrandInteractions,
            List<BasePair> stackingInteractions,
            List<BasePair> basePhosphateInteractions,
            List<BasePair> baseRiboseInteractions, List<BaseTriple> baseTriples) {
        this.modelNumber = modelNumber;
        this.output2D = output2D;
        this.messages = messages;
        this.canonicalInteractions = canonicalInteractions;
        this.nonCanonicalInteractions = nonCanonicalInteractions;
        this.interStrandInteractions = interStrandInteractions;
        this.stackingInteractions = stackingInteractions;
        this.basePhosphateInteractions = basePhosphateInteractions;
        this.baseRiboseInteractions = baseRiboseInteractions;
        this.baseTriples = baseTriples;
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
                .setBaseTriples(engineModelResponse.getBaseTriples())
                .build();
    }

    public Output2D<T> getOutput2D() {
        return output2D;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<BasePair> getCanonicalInteractions() {
        return canonicalInteractions;
    }

    public List<BasePair> getNonCanonicalInteractions() {
        return nonCanonicalInteractions;
    }

    public List<BasePair> getInterStrandInteractions() {
        return interStrandInteractions;
    }

    public List<BasePair> getStackingInteractions() {
        return stackingInteractions;
    }

    public List<BasePair> getBasePhosphateInteractions() {
        return basePhosphateInteractions;
    }

    public List<BasePair> getBaseRiboseInteractions() {
        return baseRiboseInteractions;
    }

    public List<BaseTriple> getBaseTriples() {
        return baseTriples;
    }

    public Integer getModelNumber() {
        return modelNumber;
    }

    public static class Builder<T extends ImageInformation> {
        private Integer modelNumber;
        private Output2D<T> output2D;
        private List<String> messages;
        private List<BasePair> canonicalInteractions;
        private List<BasePair> nonCanonicalInteractions;
        private List<BasePair> interStrandInteractions;
        private List<BasePair> stackingInteractions;
        private List<BasePair> basePhosphateInteractions;
        private List<BasePair> baseRiboseInteractions;
        private List<BaseTriple> baseTriples;

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

        public Builder<T> setCanonicalInteractions(List<BasePair> canonicalInteractions) {
            this.canonicalInteractions = canonicalInteractions;
            return this;
        }

        public Builder<T> setNonCanonicalInteractions(List<BasePair> nonCanonicalInteractions) {
            this.nonCanonicalInteractions = nonCanonicalInteractions;
            return this;
        }

        public Builder<T> setInterStrandInteractions(List<BasePair> interStrandInteractions) {
            this.interStrandInteractions = interStrandInteractions;
            return this;
        }

        public Builder<T> setStackingInteractions(List<BasePair> stackingInteractions) {
            this.stackingInteractions = stackingInteractions;
            return this;
        }

        public Builder<T> setBasePhosphateInteractions(List<BasePair> basePhosphateInteractions) {
            this.basePhosphateInteractions = basePhosphateInteractions;
            return this;
        }

        public Builder<T> setBaseRiboseInteractions(List<BasePair> baseRiboseInteractions) {
            this.baseRiboseInteractions = baseRiboseInteractions;
            return this;
        }

        public Builder<T> setBaseTriples(List<BaseTriple> baseTriples) {
            this.baseTriples = baseTriples;
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
                    baseRiboseInteractions, baseTriples);
        }
    }
}
