package io.hhplus.ecommerce.global;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommonApiResponses<T> {

	@JsonUnwrapped
	private final CommonApiResponse<List<T>> response;
	private final PageInfo pageInfo;

	private CommonApiResponses(int code, String message, List<T> content, PageInfo pageInfo) {
		this.response = CommonApiResponse.of(code, message, content);
		this.pageInfo = pageInfo;
	}

	public static <T, R> CommonApiResponses<R> of(int code, String message, final Page<T> page, Function<T, R> mapper) {
		final List<R> content = page.map(mapper).getContent();
		return new CommonApiResponses<>(code, message, content, PageInfo.from(page));
	}

	public static <T, R> CommonApiResponses<R> ok(final Page<T> page, Function<T, R> mapper) {
		return of(HttpStatus.OK.value(), null, page, mapper);
	}

	@Builder
	@RequiredArgsConstructor
	@Getter
	private static class PageInfo {

		private final int page;
		private final int size;
		private final long totalElements;
		private final int totalPages;
		private final boolean hasNext;
		private final boolean hasPrevious;

		public static <T> PageInfo from(final Page<T> page) {
			return PageInfo.builder()
				.page(page.getNumber())
				.size(page.getSize())
				.totalElements(page.getTotalElements())
				.totalPages(page.getTotalPages())
				.hasNext(page.hasNext())
				.hasPrevious(page.hasPrevious())
				.build();
		}
	}
}
