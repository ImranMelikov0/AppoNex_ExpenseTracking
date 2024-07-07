package com.imranmelikov.apponex_trackingexpense.presentation.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentAddTransactionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {
    private lateinit var binding:FragmentAddTransactionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddTransactionBinding.inflate(inflater,container,false)
        return binding.root
    }

}