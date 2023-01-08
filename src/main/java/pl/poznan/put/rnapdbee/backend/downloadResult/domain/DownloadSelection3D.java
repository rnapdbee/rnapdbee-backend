package pl.poznan.put.rnapdbee.backend.downloadResult.domain;

import java.util.List;

/**
 * Class indicates 3D scenario results resources to download.
 */
public class DownloadSelection3D {
    private final List<SingleDownloadSelection3D> models;

    public DownloadSelection3D(List<SingleDownloadSelection3D> models) {
        this.models = models;
    }

    public List<SingleDownloadSelection3D> getModels() {
        return models;
    }

    public static class SingleDownloadSelection3D {
        private final DownloadSelection2D output2D;
        private final boolean messages;
        private final boolean canonicalInteractions;
        private final boolean nonCanonicalInteractions;
        private final boolean interStrandInteractions;
        private final boolean stackingInteractions;
        private final boolean basePhosphateInteractions;
        private final boolean baseRiboseInteractions;

        public SingleDownloadSelection3D(
                DownloadSelection2D output2D,
                boolean messages,
                boolean canonicalInteractions,
                boolean nonCanonicalInteractions,
                boolean interStrandInteractions,
                boolean stackingInteractions,
                boolean basePhosphateInteractions,
                boolean baseRiboseInteractions) {
            this.output2D = output2D;
            this.messages = messages;
            this.canonicalInteractions = canonicalInteractions;
            this.nonCanonicalInteractions = nonCanonicalInteractions;
            this.interStrandInteractions = interStrandInteractions;
            this.stackingInteractions = stackingInteractions;
            this.basePhosphateInteractions = basePhosphateInteractions;
            this.baseRiboseInteractions = baseRiboseInteractions;
        }

        public DownloadSelection2D getOutput2D() {
            return output2D;
        }

        public boolean isMessages() {
            return messages;
        }

        public boolean isCanonicalInteractions() {
            return canonicalInteractions;
        }

        public boolean isNonCanonicalInteractions() {
            return nonCanonicalInteractions;
        }

        public boolean isInterStrandInteractions() {
            return interStrandInteractions;
        }

        public boolean isStackingInteractions() {
            return stackingInteractions;
        }

        public boolean isBasePhosphateInteractions() {
            return basePhosphateInteractions;
        }

        public boolean isBaseRiboseInteractions() {
            return baseRiboseInteractions;
        }
    }
}
