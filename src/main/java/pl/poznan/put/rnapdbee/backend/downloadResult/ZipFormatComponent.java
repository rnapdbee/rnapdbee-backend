package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ZipFormatComponent {

    private static final Logger logger = LoggerFactory.getLogger(ZipFormatComponent.class);

    public String strandsZipFormat(List<SingleStrand> strands) {
        return strands.stream()
                .map(SingleStrand::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String bpSeqZipFormat(List<String> bpSeq) {
        return bpSeq.stream()
                .map(seq -> seq + System.lineSeparator())
                .collect(Collectors.joining());
    }

    public String ctZipFormat(List<String> ct) {
        return ct.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    public String interactionsZipFormat(List<String> interactions) {
        return interactions.stream()
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());
    }

    public String structuralElementsZipFormat(
            List<String> stems,
            List<String> loops,
            List<String> singleStrands,
            List<String> singleStrands5p,
            List<String> singleStrands3p) {
        List<String> stringList = new ArrayList<>();

        if (stems != null && !stems.isEmpty())
            stringList.add(stems.stream()
                    .map(line -> "Stem " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (loops != null && !loops.isEmpty())
            stringList.add(loops.stream()
                    .map(line -> "Loop " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands != null && !singleStrands.isEmpty())
            stringList.add(singleStrands.stream()
                    .map(line -> "Single strand " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands5p != null && !singleStrands5p.isEmpty())
            stringList.add(singleStrands5p.stream()
                    .map(line -> "Single strand 5' " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        if (singleStrands3p != null && !singleStrands3p.isEmpty())
            stringList.add(singleStrands3p.stream()
                    .map(line -> "Single strand 3' " + line + System.lineSeparator())
                    .collect(Collectors.joining()));

        return stringList.stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
