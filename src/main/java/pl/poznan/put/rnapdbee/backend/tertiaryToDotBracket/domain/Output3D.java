package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationOutput;

import java.util.ArrayList;
import java.util.List;

public class Output3D<T extends ImageInformationOutput> {
    private final List<SingleTertiaryModelOutput<T>> models;
    private final String title;

    protected Output3D(
            List<SingleTertiaryModelOutput<T>> models,
            String title) {
        this.models = models;
        this.title = title;
    }

    public List<SingleTertiaryModelOutput<T>> getModels() {
        return models;
    }

    public String getTitle() {
        return title;
    }

    public static class Builder<T extends ImageInformationOutput> {
        private List<SingleTertiaryModelOutput<T>> models = new ArrayList<>();
        private String title;

        public Builder<T> withModels(List<SingleTertiaryModelOutput<T>> models) {
            this.models = models;
            return this;
        }

        public Builder<T> addModel(SingleTertiaryModelOutput<T> model) {
            this.models.add(model);
            return this;
        }

        public Builder<T> withTitle(String title) {
            this.title = title;
            return this;
        }

        public Output3D<T> build() {
            return new Output3D<>(
                    this.getModels(),
                    this.getTitle());
        }

        public List<SingleTertiaryModelOutput<T>> getModels() {
            return models;
        }

        public String getTitle() {
            return title;
        }
    }
}
