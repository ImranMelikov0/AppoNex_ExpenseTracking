package com.imranmelikov.apponex_trackingexpense.presentation.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentEditTransactionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTransactionFragment : Fragment() {
    private lateinit var binding:FragmentEditTransactionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentEditTransactionBinding.inflate(inflater,container,false)
        return binding.root
    }
}