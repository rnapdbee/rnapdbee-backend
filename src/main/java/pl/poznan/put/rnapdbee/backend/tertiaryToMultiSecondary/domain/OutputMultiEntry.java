package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;


import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformation;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;

import java.util.List;

/**
 * Class representing Output Multi Entry
 */
public class OutputMultiEntry<T extends ImageInformation> {
    private final Output2D<T> output2D;
    private final List<AnalysisTool> adapterEnums;

    private OutputMultiEntry(
            Output2D<T> output2D,
            List<AnalysisTool> adapterEnums) {
        this.output2D = output2D;
        this.adapterEnums = adapterEnums;
    }

    public static OutputMultiEntry<ImageInformationPath> of(
            Output2D<ImageInformationPath> output2D,
            List<AnalysisTool> adapterEnums
    ) {
        return new OutputMultiEntry.Builder<ImageInformationPath>()
                .withOutput2D(output2D)
                .withAdapterEnums(adapterEnums)
                .build();
    }

    public Output2D<T> getOutput2D() {
        return output2D;
    }

    public List<AnalysisTool> getAdapterEnums() {
        return adapterEnums;
    }

    public static class Builder<T extends ImageInformation> {
        private Output2D<T> output2D;

        private List<AnalysisTool> adapterEnums;

        public Builder<T> withOutput2D(Output2D<T> output2D) {
            this.output2D = output2D;
            return this;
        }

        public Builder<T> withAdapterEnums(List<AnalysisTool> adapterEnums) {
            this.adapterEnums = adapterEnums;
            return this;
        }

        public OutputMultiEntry<T> build() {
            return new OutputMultiEntry<>(
                    this.getOutput2D(),
                    this.getAdapterEnums());
        }

        public Output2D<T> getOutput2D() {
            return output2D;
        }

        public List<AnalysisTool> getAdapterEnums() {
            return adapterEnums;
        }
    }
}
