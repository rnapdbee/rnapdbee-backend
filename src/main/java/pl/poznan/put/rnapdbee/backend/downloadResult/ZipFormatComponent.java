package pl.poznan.put.rnapdbee.backend.downloadResult;

import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BasePair;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.NamedResidue;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.BaseTriple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
class ZipFormatComponent {

    public String strandsZipFormat(List<SingleStrand> strands) {
        return strands.stream()
                .map(SingleStrand::toString)
                .collect(Collectors.joining("\n"));
    }

    public String bpSeqZipFormat(List<String> bpSeq) {
        return String.join("\n", bpSeq) + "\n";
    }

    public String ctZipFormat(List<String> ct) {
        return String.join("\n", ct) + "\n";
    }

    public String interactionsZipFormat(List<String> interactions) {
        return String.join("\n", interactions) + "\n";
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
                    .map(line -> "Stem " + line + "\n")
                    .collect(Collectors.joining()));

        if (loops != null && !loops.isEmpty())
            stringList.add(loops.stream()
                    .map(line -> "Loop " + line + "\n")
                    .collect(Collectors.joining()));

        if (singleStrands != null && !singleStrands.isEmpty())
            stringList.add(singleStrands.stream()
                    .map(line -> "Single strand " + line + "\n")
                    .collect(Collectors.joining()));

        if (singleStrands5p != null && !singleStrands5p.isEmpty())
            stringList.add(singleStrands5p.stream()
                    .map(line -> "Single strand 5' " + line + "\n")
                    .collect(Collectors.joining()));

        if (singleStrands3p != null && !singleStrands3p.isEmpty())
            stringList.add(singleStrands3p.stream()
                    .map(line -> "Single strand 3' " + line + "\n")
                    .collect(Collectors.joining()));

        return String.join("\n", stringList);
    }

    public String messagesZipFormat(List<String> messages) {
        return String.join("\n", messages) + "\n";
    }

    public String basePairsToCSV(List<BasePair> basePairs) {
        return basePairsToCSV(basePairs, 'N');
    }

    public String canonicalBasePairsToCSV(List<BasePair> basePairs) {
        return basePairsToCSV(basePairs, 'Y');
    }

    private String basePairsToCSV(
            List<BasePair> basePairs,
            char canonical) {
        char delimiter = ';';

        StringBuilder stringBuilder = new StringBuilder("Base-pair;" +
                "Interaction type;" +
                "Canonical;" +
                "Saenger;" +
                "Leontis-Westhof;" +
                "BPh;" +
                "BR")
                .append("\n");

        for (BasePair basePair : basePairs) {
            String basePairResides = basePairResides(basePair.getLeftResidue(), basePair.getRightResidue());
            String iteractionType = prepareInteractionType(basePair.getInteractionType());
            String saenger = prepareSaenger(basePair.getSaenger());

            String leontisWesthof = prepareLeontisWesthof(basePair.getLeontisWesthof());
            String BPh = prepareBPh(basePair.getbPh());
            String BR = prepareBR(basePair.getBr());

            stringBuilder.append(basePairResides)
                    .append(delimiter).append(iteractionType)
                    .append(delimiter).append(canonical)
                    .append(delimiter).append(saenger)
                    .append(delimiter).append(leontisWesthof)
                    .append(delimiter).append(BPh)
                    .append(delimiter).append(BR)
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    private String basePairResides(
            NamedResidue leftResidue,
            NamedResidue rightResidue) {
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

    private String prepareLeontisWesthof(String leontisWesthof) {
        if (leontisWesthof == null || leontisWesthof.equals("UNKNOWN"))
            return "n/a";
        return leontisWesthof;
    }

    private String prepareBPh(String BPh) {
        if (BPh == null || BPh.equals("UNKNOWN"))
            return "n/a";
        return BPh;
    }

    private String prepareBR(String BR) {
        if (BR == null || BR.equals("UNKNOWN"))
            return "n/a";
        return BR;
    }

    public String baseTriplesToCSV(List<BaseTriple> baseTriples) {
        char delimiter = ';';

        StringBuilder stringBuilder = new StringBuilder("Residue;Type;FirstPartner;SecondPartner").append("\n");

        for (BaseTriple triple : baseTriples) {
            String residue = triple.getResidue() == null ? "n/a" : triple.getResidue().toString();
            String type = triple.getType() == null ? "n/a" : triple.getType();
            String first = triple.getFirstPartner() == null ? "n/a" : triple.getFirstPartner().toString();
            String second = triple.getSecondPartner() == null ? "n/a" : triple.getSecondPartner().toString();

            stringBuilder.append(residue)
                    .append(delimiter).append(type)
                    .append(delimiter).append(first)
                    .append(delimiter).append(second)
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
