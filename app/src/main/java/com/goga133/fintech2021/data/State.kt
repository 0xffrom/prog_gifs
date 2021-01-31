package com.goga133.fintech2021.data

/**
 * Состояние загрузки.
 */
data class State<T>(val event: Event, val throwable: Throwable? = null, val data: T? = null)
