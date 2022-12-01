package pl.poznan.put.rnapdbee.backend.shared.domain;

import java.util.List;


public class Output2D<T extends ImageInformationOutput> {
    private final List<Object> strands;
    private final List<String> bpSeq;
    private final List<String> ct;
    private final List<String> interactions;
    private final Object structuralElements;
    private final T imageInformation;

    protected Output2D(
            List<Object> strands,
            List<String> bpSeq,
            List<String> ct,
            List<String> interactions,
            Object structuralElements,
            T imageInformation) {
        this.strands = strands;
        this.bpSeq = bpSeq;
        this.ct = ct;
        this.interactions = interactions;
        this.structuralElements = structuralElements;
        this.imageInformation = imageInformation;
    }

    public List<Object> getStrands() {
        return strands;
    }

    public List<String> getBpSeq() {
        return bpSeq;
    }

    public List<String> getCt() {
        return ct;
    }

    public List<String> getInteractions() {
        return interactions;
    }

    public Object getStructuralElements() {
        return structuralElements;
    }

    public T getImageInformation() {
        return imageInformation;
    }

    public static class Builder<T extends ImageInformationOutput> {

        private List<Object> strands;
        private List<String> bpSeq;
        private List<String> ct;
        private List<String> interactions;
        private Object structuralElements;
        private T imageInformation;

        public Builder<T> withStrands(List<Object> strands) {
            this.strands = strands;
            return this;
        }

        public Builder<T> withBpSeq(List<String> bpSeq) {
            this.bpSeq = bpSeq;
            return this;
        }

        public Builder<T> withCt(List<String> ct) {
            this.ct = ct;
            return this;
        }

        public Builder<T> withInteractions(List<String> interactions) {
            this.interactions = interactions;
            return this;
        }

        public Builder<T> withStructuralElement(Object structuralElements) {
            this.structuralElements = structuralElements;
            return this;
        }

        public Builder<T> withImageInformation(T imageInformation) {
            this.imageInformation = imageInformation;
            return this;
        }

        public Output2D<T> build() {
            return new Output2D<T>(
                    this.getStrands(),
                    this.getBpSeq(),
                    this.getCt(),
                    this.getInteractions(),
                    this.getStructuralElements(),
                    this.getImageInformation());
        }

        public List<Object> getStrands() {
            return strands;
        }

        public List<String> getBpSeq() {
            return bpSeq;
        }

        public List<String> getCt() {
            return ct;
        }

        public List<String> getInteractions() {
            return interactions;
        }

        public Object getStructuralElements() {
            return structuralElements;
        }

        public T getImageInformation() {
            return imageInformation;
        }
    }
}
