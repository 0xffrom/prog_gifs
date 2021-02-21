package com.goga133.prog_gifs.data

/**
 * Wrapper под запросы для [PageSection.HOT], [PageSection.LATEST], [PageSection.TOP]
 */
data class ResponseWrapper(val result: Collection<Gif>, val totalCount: Int)