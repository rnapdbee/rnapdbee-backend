package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;


import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationOutput;

import java.util.ArrayList;
import java.util.List;


/**
 * DTO class for OutputMulti
 */
public class OutputMulti<T extends ImageInformationOutput, U extends ConsensualVisualization> {
    private final List<OutputMultiEntry<T>> entries;
    private final String title;
    private final U consensualVisualization;

    protected OutputMulti(
            List<OutputMultiEntry<T>> entries,
            String title,
            U consensualVisualization) {
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

    public U getConsensualVisualization() {
        return consensualVisualization;
    }

    public static class Builder<T extends ImageInformationOutput, U extends ConsensualVisualization> {
        private List<OutputMultiEntry<T>> entries = new ArrayList<>();
        private String title;
        private U consensualVisualization;

        public Builder<T, U> withEntries(List<OutputMultiEntry<T>> entries) {
            this.entries = entries;
            return this;
        }

        public Builder<T, U> addEntry(OutputMultiEntry<T> entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder<T, U> withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder<T, U> withConsensualVisualization(U consensualVisualization) {
            this.consensualVisualization = consensualVisualization;
            return this;
        }

        public OutputMulti<T, U> build() {
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

        public U getConsensualVisualization() {
            return consensualVisualization;
        }
    }
}
