package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.NamedResidue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
class ZipFormatComponent {

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

    public String messagesZipFormat(List<String> messages) {
        return messages.stream()
                .map(message -> message + System.lineSeparator())
                .collect(Collectors.joining());
    }

    public String basePairsToCSV(List<BasePair> basePairs) {
        char delimiter = ';';
        StringBuilder stringBuilder =
                new StringBuilder("Base-pair;" +
                        "Interaction type;" +
                        "Canonical;" +
                        "Saenger;" +
                        "Leontis-Westhof;" +
                        "BPh;" +
                        "BR;" +
                        "Represented in dot-bracket")
                        .append(System.lineSeparator());

        for (BasePair basePair : basePairs) {
            String basePairResides = basePairResides(basePair.getLeftResidue(), basePair.getRightResidue());
            String iteractionType = prepareInteractionType(basePair.getInteractionType());
            String saenger = prepareSaenger(basePair.getSaenger());
            String canonical = isSaengerCanonical(saenger);

            String leontisWesthof = prepareLeontisWesthof(basePair.getLeontisWesthof());
            String BPh = prepareBPh(basePair.getbPh());
            String BR = prepareBR(basePair.getBr());

            //TODO
            String representedInDotBracket = "N";

            stringBuilder.append(basePairResides)
                    .append(delimiter).append(iteractionType)
                    .append(delimiter).append(saenger)
                    .append(delimiter).append(canonical)
                    .append(delimiter).append(leontisWesthof)
                    .append(delimiter).append(BPh)
                    .append(delimiter).append(BR)
                    .append(delimiter).append(representedInDotBracket)
                    .append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    private String basePairResides(
            NamedResidue leftResidue,
            NamedResidue rightResidue
    ) {
        return leftResidue.toString() + " - " + rightResidue.toString();
    }

    private String prepareInteractionType(String iteractionType) {
        if (iteractionType == null)
            return "base - base";
        return iteractionType.toLowerCase();
    }

    private String prepareSaenger(String saenger) {
        if (saenger == null || saenger.equals("UNKNOWN"))
            return "n/a";
        return saenger;
    }

    private String isSaengerCanonical(String saenger) {
        return Objects.equals(saenger, "XIX")
                || Objects.equals(saenger, "XX")
                || Objects.equals(saenger, "XXVIII") ? "Y" : "N";
    }

    private String prepareLeontisWesthof(String leontisWesthof) {
        if (leontisWesthof == null || leontisWesthof.equals("UNKNOWN"))
            return "n/a";
        return leontisWesthof;
    }

    private String prepareBPh(String BPh) {
        return Objects.requireNonNullElse(BPh, "UNKNOWN");
    }

    private String prepareBR(String BR) {
        return Objects.requireNonNullElse(BR, "UNKNOWN");
    }
}
