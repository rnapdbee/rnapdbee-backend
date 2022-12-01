package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;

import java.util.List;

/**
 * DTO class for SingleTertiaryModelOutput
 */
public class SingleTertiaryModelOutput {

    @JsonProperty("modelNumber")
    private Integer modelNumber;

    @JsonProperty("output2D")
    private Output2D output2D;

    @JsonProperty("messages")
    private List<String> messages;

    @JsonProperty("canonicalInteractions")
    private List<Object> canonicalInteractions;

    @JsonProperty("nonCanonicalInteractions")
    private List<Object> nonCanonicalInteractions;

    @JsonProperty("interStrandInteractions")
    private List<Object> interStrandInteractions;

    @JsonProperty("stackingInteractions")
    private List<Object> stackingInteractions;

    @JsonProperty("basePhosphateInteractions")
    private List<Object> basePhosphateInteractions;

    @JsonProperty("baseRiboseInteractions")
    private List<Object> baseRiboseInteractions;

    private SingleTertiaryModelOutput(
            Integer modelNumber,
            Output2D output2D,
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

    public Output2D getOutput2D() {
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

    public static class Builder {
        private Integer modelNumber;
        private Output2D output2D;
        private List<String> messages;
        private List<Object> canonicalInteractions;
        private List<Object> nonCanonicalInteractions;
        private List<Object> interStrandInteractions;
        private List<Object> stackingInteractions;
        private List<Object> basePhosphateInteractions;
        private List<Object> baseRiboseInteractions;

        public Builder setModelNumber(Integer modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        public Builder setOutput2D(Output2D output2D) {
            this.output2D = output2D;
            return this;
        }

        public Builder setMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public Builder setCanonicalInteractions(List<Object> canonicalInteractions) {
            this.canonicalInteractions = canonicalInteractions;
            return this;
        }

        public Builder setNonCanonicalInteractions(List<Object> nonCanonicalInteractions) {
            this.nonCanonicalInteractions = nonCanonicalInteractions;
            return this;
        }

        public Builder setInterStrandInteractions(List<Object> interStrandInteractions) {
            this.interStrandInteractions = interStrandInteractions;
            return this;
        }

        public Builder setStackingInteractions(List<Object> stackingInteractions) {
            this.stackingInteractions = stackingInteractions;
            return this;
        }

        public Builder setBasePhosphateInteractions(List<Object> basePhosphateInteractions) {
            this.basePhosphateInteractions = basePhosphateInteractions;
            return this;
        }

        public Builder setBaseRiboseInteractions(List<Object> baseRiboseInteractions) {
            this.baseRiboseInteractions = baseRiboseInteractions;
            return this;
        }

        public SingleTertiaryModelOutput build() {
            return new SingleTertiaryModelOutput(modelNumber,
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
