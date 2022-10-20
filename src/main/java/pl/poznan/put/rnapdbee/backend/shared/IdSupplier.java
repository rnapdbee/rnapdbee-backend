package pl.poznan.put.rnapdbee.backend.shared;

import java.util.UUID;

public abstract class IdSupplier {
    public static UUID generateId() {
        return UUID.randomUUID();
    }
}
