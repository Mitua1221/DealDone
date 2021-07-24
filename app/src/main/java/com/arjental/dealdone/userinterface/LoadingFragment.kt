package com.arjental.dealdone.userinterface

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arjental.dealdone.DealDoneApplication
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoadingFragment : Fragment() {

    @Inject lateinit var translator: Translator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as DealDoneApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.loading_fragment, container, false)

        lifecycleScope.launch { translator.actualTaskList.collect {
            findNavController().navigate(R.id.action_loadingFragment_to_dealsFragment)
        } }

        return view
    }

}