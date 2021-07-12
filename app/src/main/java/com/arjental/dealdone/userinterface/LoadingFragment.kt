package com.arjental.dealdone.userinterface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.loading_fragment, container, false)

        lifecycleScope.launch { Translator.taskListFlow.collect {
            findNavController().navigate(R.id.action_loadingFragment_to_dealsFragment)
        } }

//        Translator.taskList.observe(viewLifecycleOwner, {
//            if (it != null) findNavController().navigate(R.id.action_loadingFragment_to_dealsFragment)
//        })

        return view
    }

}