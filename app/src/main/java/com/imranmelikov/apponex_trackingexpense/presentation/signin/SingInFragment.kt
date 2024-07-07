package com.imranmelikov.apponex_trackingexpense.presentation.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.imranmelikov.apponex_trackingexpense.presentation.LoginActivity
import com.imranmelikov.apponex_trackingexpense.presentation.MainActivity
import com.imranmelikov.apponex_trackingexpense.constants.EditTextEmptyConstants
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentSingInBinding
import com.imranmelikov.apponex_trackingexpense.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingInFragment : Fragment() {
    private lateinit var binding:FragmentSingInBinding
    private lateinit var viewModel:SignInViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSingInBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity())[SignInViewModel::class.java]

        observeCRUD()
        signIn()
        binding.dontHaveAccount.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private fun signIn(){
        val email=binding.emailEdittext
        val password=binding.passwordEdittext
        binding.saveBtn.setOnClickListener {
            when{
                email.text.isNotEmpty()&&password.text.isNotEmpty()->{
                    viewModel.signIn(email.text.toString(), password.text.toString())
                }
                email.text.isEmpty()->{
                    Toast.makeText(requireContext(), EditTextEmptyConstants.emailEmpty, Toast.LENGTH_SHORT).show()
                }
                password.text.isEmpty()->{
                    Toast.makeText(requireContext(), EditTextEmptyConstants.passwordEmpty, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun observeCRUD(){
        viewModel.msgLiveData.observe(viewLifecycleOwner){result->
            when(result.status){
                Status.SUCCESS->{
                    result.data?.let {
                        val intent= Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        (activity as LoginActivity).finish()
                        Log.d(it.message,it.success.toString())
                    }
                }
                Status.LOADING->{
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), ErrorMsgConstants.errorForUser, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}