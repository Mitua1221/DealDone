package com.arjental.dealdone.userinterface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator

class LoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.loading_fragment, container, false)

        Translator.taskList.observe(viewLifecycleOwner, {
            if (it != null) findNavController().navigate(R.id.action_loadingFragment_to_dealsFragment)
        })

        return view
    }

}