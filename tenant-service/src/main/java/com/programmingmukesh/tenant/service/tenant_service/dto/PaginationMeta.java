package com.programmingmukesh.tenant.service.tenant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Pagination metadata for paginated API responses.
 * 
 * <p>
 * This class provides pagination information including:
 * </p>
 * <ul>
 * <li>Current page information</li>
 * <li>Total counts and pages</li>
 * <li>Navigation flags</li>
 * </ul>
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
public class PaginationMeta {

  /**
   * Current page number (0-based).
   */
  private int page;

  /**
   * Number of items per page.
   */
  private int size;

  /**
   * Total number of items across all pages.
   */
  private long totalElements;

  /**
   * Total number of pages.
   */
  private int totalPages;

  /**
   * Number of items in the current page.
   */
  private int numberOfElements;

  /**
   * Flag indicating if this is the first page.
   */
  private boolean first;

  /**
   * Flag indicating if this is the last page.
   */
  private boolean last;

  /**
   * Flag indicating if there is a next page.
   */
  private boolean hasNext;

  /**
   * Flag indicating if there is a previous page.
   */
  private boolean hasPrevious;

  /**
   * Creates pagination metadata from Spring Data Page information.
   * 
   * @param page             the current page number
   * @param size             the page size
   * @param totalElements    the total number of elements
   * @param totalPages       the total number of pages
   * @param numberOfElements the number of elements in current page
   * @param first            whether this is the first page
   * @param last             whether this is the last page
   * @return pagination metadata
   */
  public static PaginationMeta of(int page, int size, long totalElements, int totalPages,
      int numberOfElements, boolean first, boolean last) {
    return PaginationMeta.builder()
        .page(page)
        .size(size)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .numberOfElements(numberOfElements)
        .first(first)
        .last(last)
        .hasNext(!last)
        .hasPrevious(!first)
        .build();
  }

  /**
   * Creates pagination metadata from Spring Data Page.
   * 
   * @param springPage the Spring Data Page object
   * @return pagination metadata
   */
  public static PaginationMeta fromPage(org.springframework.data.domain.Page<?> springPage) {
    return of(
        springPage.getNumber(),
        springPage.getSize(),
        springPage.getTotalElements(),
        springPage.getTotalPages(),
        springPage.getNumberOfElements(),
        springPage.isFirst(),
        springPage.isLast());
  }
}
