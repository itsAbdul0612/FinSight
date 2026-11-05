package com.technerd.finsight.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@Schema(name = "ApiError", description = "Error payload returned by the API.")
public class ApiError {

    @Schema(description = "Human-readable error message", example = "Input Validation Error.")
    private String message;
    @Schema(description = "HTTP status associated with the error", example = "BAD_REQUEST")
    private HttpStatus status;
    @Schema(description = "Validation or other detail messages")
    private List<String> subErrors;
}
