package pl.poznan.put.rnapdbee.backend.downloadResult.domain;

/**
 * Class indicates the Output2D resources to download.
 */
public class DownloadSelection2D {
    private final boolean strands;
    private final boolean bpSeq;
    private final boolean ct;
    private final boolean interactions;
    private final boolean structuralElements;
    private final boolean imageInformation;

    public DownloadSelection2D(
            boolean strands,
            boolean bpSeq,
            boolean ct,
            boolean interactions,
            boolean structuralElements,
            boolean imageInformation) {
        this.strands = strands;
        this.bpSeq = bpSeq;
        this.ct = ct;
        this.interactions = interactions;
        this.structuralElements = structuralElements;
        this.imageInformation = imageInformation;
    }

    public boolean isStrands() {
        return strands;
    }

    public boolean isBpSeq() {
        return bpSeq;
    }

    public boolean isCt() {
        return ct;
    }

    public boolean isInteractions() {
        return interactions;
    }

    public boolean isStructuralElements() {
        return structuralElements;
    }

    public boolean isImageInformation() {
        return imageInformation;
    }
}
