package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageProvider {
    protected final MessageSource messageSource;

    protected MessageProvider(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        return messageSource.getMessage(
                code,
                null,
                LocaleContextHolder.getLocale());
    }
}
