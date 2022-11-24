package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import java.util.List;

public class EngineOutput2DResponse {
    private List<Object> strands;

    private List<String> bpSeq;

    private List<String> ct;

    private List<String> interactions;

    private Object structuralElements;

    private EngineImageInformationOutputResponse imageInformation;

    public EngineImageInformationOutputResponse getImageInformation() {
        return imageInformation;
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
}
