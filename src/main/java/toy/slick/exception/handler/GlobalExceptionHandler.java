package toy.slick.exception.handler;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import toy.slick.common.Response;
import toy.slick.exception.BlankException;
import toy.slick.exception.EmptyException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public Response<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info(e.getMessage(), e);

        String data = e.getAllErrors()
                .stream()
                .map(error -> error.getObjectName() + ": " + error.getCode())
                .collect(Collectors.joining(", ", "[", "]"));

        return new Response<>(Response.Status.VALIDATION_ERROR.getCode(), Response.Status.VALIDATION_ERROR.getMessage(), data);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class
    })
    public Response<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info(e.getMessage(), e);

        return new Response<>(Response.Status.VALIDATION_ERROR.getCode(), Response.Status.VALIDATION_ERROR.getMessage(), "[" + e.getName() + ": " + e.getErrorCode() + "]");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            MongoWriteException.class
    })
    public Response<String> handleMongoWriteException(MongoWriteException e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.MONGO_WRITE_ERROR.getCode(), Response.Status.MONGO_WRITE_ERROR.getMessage(), e.getError().getCategory().name());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            BlankException.class,
            EmptyException.class,
            NullPointerException.class
    })
    public Response<String> handleNullPointerException(NullPointerException e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.NULL_ERROR.getCode(), Response.Status.NULL_ERROR.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            Exception.class
    })
    public Response<String> handleException(Exception e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.UNKNOWN_ERROR.getCode(), Response.Status.UNKNOWN_ERROR.getMessage(), null);
    }
}
