package com.goga133.fintech2021.business_logic

import com.goga133.fintech2021.R
import com.goga133.fintech2021.data.PageInfo
import com.goga133.fintech2021.data.PageSection

class PageInfoFactory {
    companion object {
        val pages: Set<PageInfo> = setOf(
            PageInfo(R.string.tab_random, PageSection.RANDOM),
            PageInfo(R.string.tab_top, PageSection.TOP),
            PageInfo(R.string.tab_latest, PageSection.LATEST),
            PageInfo(R.string.tab_hot, PageSection.HOT)
        )
    }
}