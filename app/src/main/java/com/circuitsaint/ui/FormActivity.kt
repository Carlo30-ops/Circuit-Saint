package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.circuitsaint.databinding.ActivityFormBinding
import com.circuitsaint.domain.Result
import com.circuitsaint.util.isValidEmail
import com.circuitsaint.util.isValidName
import com.circuitsaint.viewmodel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormBinding
    private val viewModel: StoreViewModel by viewModels()

    companion object {
        const val EXTRA_QR_DATA = "extra_qr_data"
        fun newIntent(context: Context, qrData: String?) = Intent(context, FormActivity::class.java).apply {
            putExtra(EXTRA_QR_DATA, qrData)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val qrData = intent.getStringExtra(EXTRA_QR_DATA)
        // If QR passed data, we can prefill fields (example: name/email)
        qrData?.let {
            // simple naive parse: look for "name":"..." and "email":"..."
            try {
                val json = org.json.JSONObject(it)
                if (json.has("name")) binding.etName.setText(json.getString("name"))
                if (json.has("email")) binding.etEmail.setText(json.getString("email"))
            } catch (e: Exception) { /* ignore */ }
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                // simulate send
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Enviar formulario con los datos ingresados?")
                    .setPositiveButton("Enviar") { _, _ ->
                        val nombre = binding.etName.text.toString()
                        val email = binding.etEmail.text.toString()
                        val mensaje = binding.etMessage.text.toString()
                        val telefono = try {
                            binding.etPhone.text?.toString()?.takeIf { it.isNotEmpty() }
                        } catch (e: Exception) {
                            null
                        }
                        
                        viewModel.submitForm(nombre, email, mensaje, telefono)
                        lifecycleScope.launch {
                            viewModel.formSubmissionState.collect { result ->
                                when (result) {
                                    is Result.Success -> {
                                        Toast.makeText(
                                            this@FormActivity,
                                            "Formulario enviado. Gracias por contactarnos.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.clearFormSubmissionState()
                                        clearForm()
                                    }
                                    is Result.Error -> {
                                        Toast.makeText(
                                            this@FormActivity,
                                            result.message ?: "Error al enviar el formulario",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.clearFormSubmissionState()
                                    }
                                    is Result.Loading -> {
                                        // Mostrar loading si es necesario
                                    }
                                    null -> {
                                        // Estado inicial
                                    }
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        val nombre = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val mensaje = binding.etMessage.text.toString()
        
        if (!nombre.isValidName()) {
            binding.etName.error = "Nombre debe tener entre 2 y 100 caracteres"
            isValid = false
        } else {
            binding.etName.error = null
        }
        
        if (email.isEmpty() || !email.isValidEmail()) {
            binding.etEmail.error = "Email válido obligatorio"
            isValid = false
        } else {
            binding.etEmail.error = null
        }
        
        if (mensaje.trim().length < 10) {
            binding.etMessage.error = "Mensaje mínimo 10 caracteres"
            isValid = false
        } else {
            binding.etMessage.error = null
        }
        
        return isValid
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etMessage.text?.clear()
        // Limpiar errores
        binding.etName.error = null
        binding.etEmail.error = null
        binding.etMessage.error = null
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
