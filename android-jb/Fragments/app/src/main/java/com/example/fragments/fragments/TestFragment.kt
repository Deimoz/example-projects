package com.example.fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fragments.R
import com.example.fragments.extensions.navigate
import kotlinx.android.synthetic.main.test_fragment.*

class TestFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clicks.text = TestFragmentArgs
            .fromBundle(requireArguments()).count
            .toString()
        button.setOnClickListener {
            navigate(TestFragmentDirections.actionTestFragmentSelf(clicks.text.toString().toInt() + 1))
        }
    }
}