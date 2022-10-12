package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;

@Service
public class SecondaryToDotBracketService {

    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;

    @Autowired
    private SecondaryToDotBracketService(SecondaryToDotBracketRepository secondaryToDotBracketRepository) {
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
    }

}
