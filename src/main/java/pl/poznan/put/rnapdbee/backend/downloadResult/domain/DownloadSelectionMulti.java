package pl.poznan.put.rnapdbee.backend.downloadResult.domain;

import java.util.List;

/**
 * Class indicates Multi scenario results resources to download.
 */
public class DownloadSelectionMulti {
    private final List<SingleDownloadSelectionMulti> entries;
    private final boolean consensualVisualization;

    public DownloadSelectionMulti(
            List<SingleDownloadSelectionMulti> entries,
            boolean consensualVisualization) {
        this.entries = entries;
        this.consensualVisualization = consensualVisualization;
    }

    public List<SingleDownloadSelectionMulti> getEntries() {
        return entries;
    }

    public boolean isConsensualVisualization() {
        return consensualVisualization;
    }

    public static class SingleDownloadSelectionMulti {
        private DownloadSelection2D output2D;

        public SingleDownloadSelectionMulti(DownloadSelection2D output2D) {
            this.output2D = output2D;
        }

        public SingleDownloadSelectionMulti() {
        }

        public DownloadSelection2D getOutput2D() {
            return output2D;
        }
    }
}
