package com.goga133.fintech2021

import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.goga133.fintech2021.databinding.ActivityMainBinding
import com.goga133.fintech2021.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var _sectionsPagerAdapter : SectionsPagerAdapter? = null
    private val sectionsPagerAdapter get() = _sectionsPagerAdapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Восстанавливаем адаптер, если можно, иначе - создаём.
        _sectionsPagerAdapter = if(savedInstanceState != null){
            savedInstanceState.getSerializable(ARG_PAGER_ADAPTER) as SectionsPagerAdapter
        } else{
            SectionsPagerAdapter(this)
        }

        val viewPager: ViewPager2 = binding.viewPager.apply {
            adapter = sectionsPagerAdapter
            isSaveEnabled = false
        }

        // Назначаем события кнопка по ссылкам на методы из SectionPagerAdapter
        binding.fabLeft.setOnClickListener(sectionsPagerAdapter::onClickLeftButton)
        binding.fabRight.setOnClickListener(sectionsPagerAdapter::onClickRightButton)

        val tabs: TabLayout = findViewById(R.id.tabs)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.getPageTitle(position)
            viewPager.setCurrentItem(tab.position, true)
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _sectionsPagerAdapter = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_PAGER_ADAPTER, sectionsPagerAdapter)
    }


    companion object{
        private const val ARG_PAGER_ADAPTER = "pager_adapter"
    }
}