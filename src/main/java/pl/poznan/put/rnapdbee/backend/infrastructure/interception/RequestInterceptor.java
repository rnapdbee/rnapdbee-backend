package pl.poznan.put.rnapdbee.backend.infrastructure.interception;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);
    private static final String UNKNOWN_ID = "UNKNOWN";
    private final MessageProvider messageProvider;

    public RequestInterceptor(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        MDC.put("RequestId", RandomStringUtils.randomAlphabetic(8).toUpperCase(Locale.ROOT));

        String id = getIdFromRequest(request);
        MDC.put("ResultId", id);

        return true;
    }

    private String getIdFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] allowedURIStart = {
                "/api/v1/engine/2d",
                "/api/v1/engine/3d",
                "/api/v1/engine/multi"};

        if (Stream.of(allowedURIStart).anyMatch(requestURI::startsWith)) {
            String[] parts = requestURI.split("/");

            if (parts.length == 6) {
                String id = parts[parts.length - 1];

                try {
                    UUID.fromString(id);
                    return id;
                } catch (IllegalArgumentException e) {
                    logger.error(String.format("Pre handled UUID: '%s' non-parsable.", id));
                    throw new IdNotFoundException(
                            messageProvider.getMessage("api.exception.id.not.found.format"), id);
                }
            } else
                return UNKNOWN_ID;
        }

        return "";
    }

    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable ModelAndView modelAndView) {
        MDC.clear();
    }
}
