package pl.poznan.put.rnapdbee.backend.downloadResult.domain;

import java.util.List;

/**
 * Class indicates 3D scenario results resources to download.
 */
public class DownloadSelection3D {
    private List<SingleDownloadSelection3D> models;

    public DownloadSelection3D(List<SingleDownloadSelection3D> models) {
        this.models = models;
    }

    public DownloadSelection3D() {
    }

    public List<SingleDownloadSelection3D> getModels() {
        return models;
    }

    public static class SingleDownloadSelection3D {
        private DownloadSelection2D output2D;
        private boolean messages;
        private boolean canonicalInteractions;
        private boolean nonCanonicalInteractions;
        private boolean interStrandInteractions;
        private boolean stackingInteractions;
        private boolean basePhosphateInteractions;
        private boolean baseRiboseInteractions;

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

        public SingleDownloadSelection3D() {
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
