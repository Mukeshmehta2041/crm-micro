package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Detailed error information for field-specific validation errors.
 * 
 * <p>
 * This class provides detailed error information for specific fields
 * or properties that failed validation.
 * </p>
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

  /**
   * The field or property name that has the error.
   */
  private String field;

  /**
   * The rejected value that caused the error.
   */
  private Object rejectedValue;

  /**
   * The error message for this specific field.
   */
  private String message;

  /**
   * The error code for this specific field error.
   */
  private String code;

  /**
   * Creates an error detail for a field validation error.
   * 
   * @param field   the field name
   * @param message the error message
   * @return error detail
   */
  public static ErrorDetail of(String field, String message) {
    return ErrorDetail.builder()
        .field(field)
        .message(message)
        .build();
  }

  /**
   * Creates an error detail with rejected value.
   * 
   * @param field         the field name
   * @param rejectedValue the rejected value
   * @param message       the error message
   * @return error detail
   */
  public static ErrorDetail of(String field, Object rejectedValue, String message) {
    return ErrorDetail.builder()
        .field(field)
        .rejectedValue(rejectedValue)
        .message(message)
        .build();
  }

  /**
   * Creates an error detail with code.
   * 
   * @param field         the field name
   * @param rejectedValue the rejected value
   * @param message       the error message
   * @param code          the error code
   * @return error detail
   */
  public static ErrorDetail of(String field, Object rejectedValue, String message, String code) {
    return ErrorDetail.builder()
        .field(field)
        .rejectedValue(rejectedValue)
        .message(message)
        .code(code)
        .build();
  }
}
