package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Class representing structure of Consensual Visualization
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ConsensualVisualization {
    protected abstract static class Builder<B extends Builder<B>> {
        protected abstract B self();

        protected abstract ConsensualVisualization build();
    }
}
