package com.metical_converter.infrasctruture.middleware;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

@Component
public class LocaleMiddleware {

    private final Logger logger= LoggerFactory.getLogger(LocaleMiddleware.class);
    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    private static final Locale DEFAULT_LOCALE = new Locale("pt"); // Portuguese as default language


    public  Locale getActualLocale() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String acceptLanguage = request.getHeader(ACCEPT_LANGUAGE_HEADER);

                if (acceptLanguage != null && acceptLanguage.startsWith("en")) {
                    return new Locale("en");
                }
            }
        } catch (Exception e) {
            logger.error( e.getMessage());
        }

        return DEFAULT_LOCALE;
    }
}

