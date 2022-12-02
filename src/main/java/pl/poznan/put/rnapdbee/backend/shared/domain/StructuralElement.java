package pl.poznan.put.rnapdbee.backend.shared.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO class representing single Structural Element output
 */
public class StructuralElement {
    private final List<String> stems;
    private final List<String> loops;
    private final List<String> singleStrands;
    private final List<String> singleStrands5p;
    private final List<String> singleStrands3p;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String coordinates;

    private StructuralElement(
            List<String> stems,
            List<String> loops,
            List<String> singleStrands,
            List<String> singleStrands5p,
            List<String> singleStrands3p,
            String coordinates) {
        this.stems = stems;
        this.loops = loops;
        this.singleStrands = singleStrands;
        this.singleStrands5p = singleStrands5p;
        this.singleStrands3p = singleStrands3p;
        this.coordinates = coordinates;
    }

    public List<String> getStems() {
        return stems;
    }

    public List<String> getLoops() {
        return loops;
    }

    public List<String> getSingleStrands() {
        return singleStrands;
    }

    public List<String> getSingleStrands5p() {
        return singleStrands5p;
    }

    public List<String> getSingleStrands3p() {
        return singleStrands3p;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public static class Builder {
        private List<String> stems = null;
        private List<String> loops = null;
        private List<String> singleStrands = null;
        private List<String> singleStrands5p = null;
        private List<String> singleStrands3p = null;
        private String coordinates = null;


        public Builder withStems(List<String> stems) {
            this.stems = stems;
            return this;
        }

        public Builder withLoops(List<String> loops) {
            this.loops = loops;
            return this;
        }

        public Builder withSingleStrands(List<String> singleStrands) {
            this.singleStrands = singleStrands;
            return this;
        }

        public Builder withSingleStrands5p(List<String> singleStrands5p) {
            this.singleStrands5p = singleStrands5p;
            return this;
        }

        public Builder withSingleStrands3p(List<String> singleStrands3p) {
            this.singleStrands3p = singleStrands3p;
            return this;
        }

        public Builder withCoordinates(String coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public StructuralElement build() {
            return new StructuralElement(
                    this.getStems(),
                    this.getLoops(),
                    this.getSingleStrands(),
                    this.getSingleStrands5p(),
                    this.getSingleStrands3p(),
                    this.getCoordinates());
        }

        public List<String> getStems() {
            return stems;
        }

        public List<String> getLoops() {
            return loops;
        }

        public List<String> getSingleStrands() {
            return singleStrands;
        }

        public List<String> getSingleStrands5p() {
            return singleStrands5p;
        }

        public List<String> getSingleStrands3p() {
            return singleStrands3p;
        }

        public String getCoordinates() {
            return coordinates;
        }
    }
}
