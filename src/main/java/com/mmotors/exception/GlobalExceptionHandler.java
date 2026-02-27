package com.mmotors.exception;

import com.mmotors.service.AlertingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * The type Global exception handler.
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final AlertingService alertingService;

    /**
     * 404 - Page non trouvée
     *
     * @param ex the ex
     * @return the model and view
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoResourceFoundException ex) {
        log.warn("404 - Ressource non trouvée : {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", "La page que vous cherchez n'existe pas.");
        return mav;
    }

    /**
     * 403 - Accès refusé
     *
     * @param ex the ex
     * @return the model and view
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        log.warn("403 - Accès refusé : {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", "Vous n'avez pas les droits pour accéder à cette page.");
        return mav;
    }

    /**
     * 500 - Erreur interne
     *
     * @param ex the ex
     * @return the model and view
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("500 - Erreur interne : {}", ex.getMessage(), ex);
        alertingService.sendCriticalAlert("Erreur 500", ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", "Une erreur est survenue. Veuillez réessayer plus tard.");
        return mav;
    }

}