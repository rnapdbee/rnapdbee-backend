package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

/**
 * Class representing structure of Consensual Visualization
 */
public abstract class ConsensualVisualization {
    protected abstract static class Builder<B extends Builder<B>> {
        protected abstract B self();

        protected abstract ConsensualVisualization build();
    }
}
