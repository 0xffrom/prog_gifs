package com.goga133.fintech2021.ui.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goga133.fintech2021.business_logic.PageInfoFactory
import com.goga133.fintech2021.business_logic.SwitchesButtons
import java.io.Serializable

class SectionsPagerAdapter(private val activity: FragmentActivity) :
    FragmentStateAdapter(activity), SwitchesButtons, Serializable {
    private var currentPosition = 0

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        currentPosition = position

        return fragments[position]
    }

    fun getPageTitle(position: Int): CharSequence {
        return activity.resources.getString(PageInfoFactory.pages.elementAt(position).resourceId)
    }

    override fun onClickLeftButton(v: View) {
        fragments[currentPosition].onClickLeftButton(v)
    }

    override fun onClickRightButton(v: View) {
        fragments[currentPosition].onClickRightButton(v)
    }

    companion object {
        val fragments = PageInfoFactory.pages.map { PlaceholderFragment.newInstance(it) }
    }
}