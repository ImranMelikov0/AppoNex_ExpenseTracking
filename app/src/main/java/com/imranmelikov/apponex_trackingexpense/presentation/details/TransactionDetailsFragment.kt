package com.imranmelikov.apponex_trackingexpense.presentation.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentTransactionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentTransactionDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }
}