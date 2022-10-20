package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository.TertiaryToMultiSecondaryRepository;

@Service
public class TertiaryToMultiSecondaryService {

    private final TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository;

    @Autowired
    private TertiaryToMultiSecondaryService(TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository) {
        this.tertiaryToMultiSecondaryRepository = tertiaryToMultiSecondaryRepository;
    }
}
