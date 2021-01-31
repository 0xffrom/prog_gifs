package com.goga133.fintech2021.ui.fragment_page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goga133.fintech2021.business_logic.PageInfoFactory

/**
 * Адаптер под ViewPager2.
 *
 * @param activity - фрагмент активити, в котором находится ViewPager2.
 */
class PagerAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {

    /**
     * Инициализируем фрагменты с помощью фабрики страниц [PageInfoFactory.PAGES]
     */
    private val fragments = PageInfoFactory.PAGES.map {
        PageFragment.newInstance(it)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    /**
     * Полулучаем название страницы с помощью фабрики страниц [PageInfoFactory.PAGES]
     */
    fun getPageTitle(position: Int): CharSequence {
        return activity.resources.getString(PageInfoFactory.PAGES.elementAt(position).resourceId)
    }
}