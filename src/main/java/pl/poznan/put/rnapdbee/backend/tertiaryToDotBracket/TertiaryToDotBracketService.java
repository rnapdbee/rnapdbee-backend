package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

@Service
public class TertiaryToDotBracketService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;

    @Autowired
    private TertiaryToDotBracketService(
            TertiaryToDotBracketRepository tertiaryToDotBracketRepository) {
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
    }
}
