package com.goga133.prog_gifs.business_logic

import com.goga133.prog_gifs.R
import com.goga133.prog_gifs.data.PageInfo
import com.goga133.prog_gifs.data.PageSection

/**
 * Фабрика страниц.
 */
class PageInfoFactory{
    companion object {

        val PAGES: Set<PageInfo> = setOf(
            PageInfo(R.string.tab_random, PageSection.RANDOM),
            PageInfo(R.string.tab_top, PageSection.TOP),
            PageInfo(R.string.tab_latest, PageSection.LATEST)
        )
    }
}