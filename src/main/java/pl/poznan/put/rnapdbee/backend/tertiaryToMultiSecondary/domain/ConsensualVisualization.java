package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;


/**
 * DTO class for Consensual Visualization
 */
public abstract class ConsensualVisualization {
    protected abstract static class Builder<B extends Builder<B>> {
        protected abstract B self();

        protected abstract ConsensualVisualization build();
    }
}
