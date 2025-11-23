package com.circuitsaint.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.circuitsaint.R
import com.circuitsaint.databinding.FragmentContactFormBinding
import com.circuitsaint.domain.Result
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactFormFragment : Fragment() {

    private var _binding: FragmentContactFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener { submitForm() }
        observeFormState()
    }

    private fun submitForm() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim().takeIf { it.isNotEmpty() }
        val message = binding.etMessage.text.toString().trim()

        if (name.isEmpty() || message.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), R.string.contact_error, Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.submitForm(name, email, message, phone)
    }

    private fun observeFormState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formSubmissionState.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(requireContext(), R.string.contact_success, Toast.LENGTH_LONG).show()
                            clearForm()
                            viewModel.clearFormSubmissionState()
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                requireContext(),
                                result.message ?: getString(R.string.contact_error),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.clearFormSubmissionState()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
        binding.etMessage.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

