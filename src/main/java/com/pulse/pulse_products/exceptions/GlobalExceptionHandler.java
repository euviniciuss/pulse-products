
package com.pulse.pulse_products.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException exception) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(
            AlreadyExistsException exception
    ) {
        return problem(
                HttpStatus.CONFLICT,
                "Conflito",
                exception.getMessage()
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setType(URI.create("about:blank"));

        return problem;
    }
}