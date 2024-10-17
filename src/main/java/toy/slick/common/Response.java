package toy.slick.common;

import lombok.Getter;

@Getter
public class Response<T> {
    @Getter
    public enum Status {
        SUCCESS("0000", "Success"),

        /* 4000 ~ 4999 : Client Request Error */
        VALIDATION_ERROR("4001", "Validation Error"),
        ALREADY_EXISTS_ERROR("4002", "Already Exists Error"),
        NOT_FOUND_ERROR("4040", "Not Found Error"),
        BUCKET4J_ERROR("4998", "Too Many Request"),
        REQUEST_API_KEY_ERROR("4999", "RequestApiKey Error"),

        /* 8000 ~ 8999 : DB Error */
        MONGO_WRITE_ERROR("8001", "DB Write Error"),

        /* 9000 ~ 9999 : System Error */
        NULL_ERROR("9998", "Null Error"),
        UNKNOWN_ERROR("9999", "Unknown Error");

        private final String code;
        private final String message;

        Status(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private final String code;
    private final String message;
    private final T data;

    public Response(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(T data) {
        this.code = Status.SUCCESS.getCode();
        this.message = Status.SUCCESS.getMessage();
        this.data = data;
    }
}
