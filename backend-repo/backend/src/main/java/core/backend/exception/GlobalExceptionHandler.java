package core.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//전역 예외 처리 클래스
@RestControllerAdvice
public class GlobalExceptionHandler {

    //CustonException 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex){
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponse(ex.getMessage()));
    }

    //@Valid 검증 실패 시 예외 처리(필수 입력값 오류 메시지 반환)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();

        //필수 입력값 누락 시, 필드별 오류 메시지 담아서 반환
        for(FieldError error : ex.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    //응답 형식(json반환)
    public static class ErrorResponse{
        private final String error;

        public ErrorResponse(String error){
            this.error = error;
        }

        public String getError(){
            return error;
        }
    }
}
