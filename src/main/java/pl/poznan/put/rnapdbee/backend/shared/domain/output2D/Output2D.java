package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing secondary structure analysis.
 */
public class Output2D<T extends ImageInformation> {
    private final List<SingleStrand> strands;
    private final List<String> bpSeq;
    private final List<String> ct;
    private final List<String> interactions;
    private final StructuralElement structuralElements;
    private final T imageInformation;

    protected Output2D(
            List<SingleStrand> strands,
            List<String> bpSeq,
            List<String> ct,
            List<String> interactions,
            StructuralElement structuralElements,
            T imageInformation) {
        this.strands = strands;
        this.bpSeq = bpSeq;
        this.ct = ct;
        this.interactions = interactions;
        this.structuralElements = structuralElements;
        this.imageInformation = imageInformation;
    }

    public static Output2D<ImageInformationPath> of(
            Output2D<ImageInformationByteArray> engineOutput2DResponse,
            ImageInformationPath imageInformationPath
    ) {
        return new Output2D.Builder<ImageInformationPath>()
                .withStrands(engineOutput2DResponse.getStrands())
                .withBpSeq(engineOutput2DResponse.getBpSeq())
                .withCt(engineOutput2DResponse.getCt())
                .withInteractions(engineOutput2DResponse.getInteractions())
                .withStructuralElement(engineOutput2DResponse.getStructuralElements())
                .withImageInformation(imageInformationPath)
                .build();
    }

    public List<SingleStrand> getStrands() {
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

    public StructuralElement getStructuralElements() {
        return structuralElements;
    }

    public T getImageInformation() {
        return imageInformation;
    }

    public static class Builder<T extends ImageInformation> {

        private List<SingleStrand> strands;
        private List<String> bpSeq;
        private List<String> ct;
        private List<String> interactions;
        private StructuralElement structuralElements;
        private T imageInformation;

        public Builder<T> withStrands(List<SingleStrand> strands) {
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

        public Builder<T> withStructuralElement(StructuralElement structuralElements) {
            if (structuralElements == null) {
                this.structuralElements = new StructuralElement(
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        null);
                return this;
            }

            this.structuralElements = structuralElements;
            return this;
        }

        public Builder<T> withImageInformation(T imageInformation) {
            this.imageInformation = imageInformation;
            return this;
        }

        public Output2D<T> build() {
            return new Output2D<>(
                    this.getStrands(),
                    this.getBpSeq(),
                    this.getCt(),
                    this.getInteractions(),
                    this.getStructuralElements(),
                    this.getImageInformation());
        }

        public List<SingleStrand> getStrands() {
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

        public StructuralElement getStructuralElements() {
            return structuralElements;
        }

        public T getImageInformation() {
            return imageInformation;
        }
    }
}
