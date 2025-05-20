package org.graduate.shoefastbe.base.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface IFilterBuilder<T> {
    Filter.FilterBuilder<T> search();
    Filter.FilterBuilder<T> filter();
    Filter.FilterBuilder<T> isContain(String fieldName, String value);
    Filter.FilterBuilder<T> isIn(String fieldName, Collection<Object> value);
    Filter.FilterBuilder<T> isNotIn(String fieldName, Collection<Object> values);
    Filter.FilterBuilder<T> isEqual(String fieldName, Object value);
    Filter.FilterBuilder<T> orderBy(String fieldName, String orderType);
    Filter.FilterBuilder<T> isNull(String fieldName);
    Filter.FilterBuilder<T> isNotNull(String fieldName);
    Filter.FilterBuilder<T> isLessThan(String fieldName, Object value);
    Filter.FilterBuilder<T> isGreaterThan(String fieldName, Object value);
    Filter.FilterBuilder<T> isLessThanOrEqual(String fieldName, Object value);
    Filter.FilterBuilder<T> isGreaterThanOrEqual(String fieldName, Object value);
    Page<T> getPage(Pageable pageable);
}
