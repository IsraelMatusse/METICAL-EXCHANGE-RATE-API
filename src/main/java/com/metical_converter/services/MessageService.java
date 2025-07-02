package com.metical_converter.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    public String getLocalizedMessage(String messageId, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        return getLocalizedMessage(messageId, locale);
    }

    public String getLocalizedMessage(String messageId, Locale locale, Object... args) {
        try {
            return messageSource.getMessage(messageId, args, locale);
        } catch (NoSuchMessageException e) {
            try {
                return messageSource.getMessage(messageId, args, new Locale("pt"));
            } catch (NoSuchMessageException e2) {
                return messageId;
            }
        }
    }

    // Mantenha também o método sem parâmetros para compatibilidade
    public String getLocalizedMessage(String messageId, Locale locale) {
        return getLocalizedMessage(messageId, locale, (Object[]) null);
    }

}