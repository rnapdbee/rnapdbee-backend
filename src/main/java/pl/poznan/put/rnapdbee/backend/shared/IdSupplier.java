package pl.poznan.put.rnapdbee.backend.shared;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Class supplying id.
 */
public abstract class IdSupplier {
    public static UUID generateId() {
        UUID uuid = UUID.randomUUID();
        MDC.put("ResultId", uuid.toString());

        return uuid;
    }
}
