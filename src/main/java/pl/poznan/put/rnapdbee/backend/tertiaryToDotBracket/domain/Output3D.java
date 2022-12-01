package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import java.util.List;

public class Output3D {
    private final List<SingleTertiaryModelOutput> models;
    private final String title;

    private Output3D(
            List<SingleTertiaryModelOutput> models,
            String title) {
        this.models = models;
        this.title = title;
    }

    public List<SingleTertiaryModelOutput> getModels() {
        return models;
    }

    public String getTitle() {
        return title;
    }

    public static class Builder {
        private List<SingleTertiaryModelOutput> models;
        private String title;

        public Builder withModels(List<SingleTertiaryModelOutput> models) {
            this.models = models;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Output3D build() {
            return new Output3D(models, title);
        }
    }
}

