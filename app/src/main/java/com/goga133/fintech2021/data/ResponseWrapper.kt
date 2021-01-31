package com.goga133.fintech2021.data

/**
 * Wrapper под запросы для [PageSection.HOT], [PageSection.LATEST], [PageSection.TOP]
 */
data class ResponseWrapper(val result: Collection<Gif>, val totalCount: Int)