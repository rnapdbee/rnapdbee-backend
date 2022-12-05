package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;


import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationOutput;

import java.util.List;


/**
 * DTO class for OutputMulti
 */
public class OutputMulti<T extends ImageInformationOutput> {
    private final List<OutputMultiEntry<T>> entries;
    private final String title;
    private final ConsensualVisualization consensualVisualization;

    protected OutputMulti(
            List<OutputMultiEntry<T>> entries,
            String title,
            ConsensualVisualization consensualVisualization) {
        this.entries = entries;
        this.title = title;
        this.consensualVisualization = consensualVisualization;
    }

    public List<OutputMultiEntry<T>> getEntries() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public ConsensualVisualization getConsensualVisualization() {
        return consensualVisualization;
    }

    public static class Builder<T extends ImageInformationOutput> {
        private List<OutputMultiEntry<T>> entries;
        private String title;
        private ConsensualVisualization consensualVisualization;

        public Builder<T> withEntries(List<OutputMultiEntry<T>> entries) {
            this.entries = entries;
            return this;
        }

        public Builder<T> withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder<T> withConsensualVisualization(ConsensualVisualization consensualVisualization) {
            this.consensualVisualization = consensualVisualization;
            return this;
        }

        public OutputMulti<T> build() {
            return new OutputMulti<>(
                    this.getEntries(),
                    this.getTitle(),
                    this.getConsensualVisualization());
        }

        public List<OutputMultiEntry<T>> getEntries() {
            return entries;
        }

        public String getTitle() {
            return title;
        }

        public ConsensualVisualization getConsensualVisualization() {
            return consensualVisualization;
        }
    }
}
