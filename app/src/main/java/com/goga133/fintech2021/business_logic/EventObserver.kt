package com.goga133.fintech2021.business_logic

/**
 * Интерфейс для реализации подписчиком методов взависимости от события.
 */
interface EventObserver<T> {
    fun onSuccess(data: T)
    fun onLoading()
    fun onError(throwable: Throwable? = null)
}