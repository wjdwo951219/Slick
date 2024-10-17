package toy.slick.exception.handler;

import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import toy.slick.common.Response;
import toy.slick.exception.AlreadyExistsException;
import toy.slick.exception.ApiKeyException;
import toy.slick.exception.BlankException;
import toy.slick.exception.Bucket4jException;
import toy.slick.exception.EmptyException;
import toy.slick.exception.QueryResultCntException;

import java.util.Arrays;
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

        Object[] detailMessageArgumentArr = e.getDetailMessageArguments();

        String data = detailMessageArgumentArr == null
                ? null
                : Arrays.stream(e.getDetailMessageArguments())
                .map(o -> o instanceof String
                        ? (String) o
                        : StringUtils.EMPTY)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining());

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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {
            ApiKeyException.class
    })
    public Response<String> handleApiKeyException(ApiKeyException e) {
        log.warn(e.getMessage(), e);

        return new Response<>(Response.Status.REQUEST_API_KEY_ERROR.getCode(), Response.Status.REQUEST_API_KEY_ERROR.getMessage(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(value = {
            Bucket4jException.class
    })
    public Response<String> handleBucket4jException(Bucket4jException e) {
        log.warn(e.getMessage(), e);

        return new Response<>(Response.Status.BUCKET4J_ERROR.getCode(), Response.Status.BUCKET4J_ERROR.getMessage(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            MongoWriteException.class
    })
    public Response<String> handleMongoWriteException(MongoWriteException e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.MONGO_WRITE_ERROR.getCode(), Response.Status.MONGO_WRITE_ERROR.getMessage(), e.getError().getCategory().name());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            AlreadyExistsException.class
    })
    public Response<String> handleAlreadyExistsException(AlreadyExistsException e) {
        log.info(e.getMessage(), e);

        return new Response<>(Response.Status.ALREADY_EXISTS_ERROR.getCode(), Response.Status.ALREADY_EXISTS_ERROR.getMessage(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            BlankException.class,
            EmptyException.class,
    })
    public Response<String> handleEmptyOrBlankException(NullPointerException e) {
        log.info(e.getMessage(), e);

        return new Response<>(Response.Status.NULL_ERROR.getCode(), Response.Status.NULL_ERROR.getMessage(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            QueryResultCntException.class
    })
    public Response<String> handleQueryResultCntException(QueryResultCntException e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.NULL_ERROR.getCode(), Response.Status.NULL_ERROR.getMessage(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            NullPointerException.class
    })
    public Response<String> handleNullPointerException(NullPointerException e) {
        log.error(e.getMessage(), e);

        return new Response<>(Response.Status.NULL_ERROR.getCode(), Response.Status.NULL_ERROR.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public Response<String> handleNoResourceFoundException(NoResourceFoundException e) {
        log.info(e.getMessage(), e);

        return new Response<>(Response.Status.NOT_FOUND_ERROR.getCode(), Response.Status.NOT_FOUND_ERROR.getMessage(), null);
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
