package com.goga133.fintech2021.ui.main

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goga133.fintech2021.R
import com.goga133.fintech2021.business_logic.SwitchesButtons

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(), SwitchesButtons {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
        })

        return root
    }

    companion object {
        private const val ARG_PAGE_INFO = "page_info"

        @JvmStatic
        fun newInstance(pageInfo: Parcelable): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAGE_INFO, pageInfo)
                }
            }
        }
    }

    override fun onClickLeftButton(v: View) {
        TODO("Not yet implemented")
    }

    override fun onClickRightButton(v: View) {
        TODO("Not yet implemented")
    }
}