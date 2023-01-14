package com.explosion204.feeds.data.mapping;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

import java.util.function.BiFunction;

@FunctionalInterface
public interface RowMapper<T> extends BiFunction<Row, RowMetadata, T> {

}
