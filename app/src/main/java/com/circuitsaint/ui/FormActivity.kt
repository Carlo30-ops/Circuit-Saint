package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.circuitsaint.databinding.ActivityFormBinding
import com.circuitsaint.viewmodel.StoreViewModel
import com.circuitsaint.viewmodel.StoreViewModelFactory

class FormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormBinding
    private val viewModel: StoreViewModel by viewModels {
        StoreViewModelFactory(application)
    }

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
                        Toast.makeText(this, "Formulario enviado. Gracias por contactarnos.", Toast.LENGTH_LONG).show()
                        clearForm()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        if (TextUtils.isEmpty(binding.etName.text)) {
            binding.etName.error = "Nombre obligatorio"
            isValid = false
        }
        if (TextUtils.isEmpty(binding.etEmail.text) || !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
            binding.etEmail.error = "Email válido obligatorio"
            isValid = false
        }
        if (TextUtils.isEmpty(binding.etMessage.text) || binding.etMessage.text.toString().trim().length < 10) {
            binding.etMessage.error = "Mensaje mínimo 10 caracteres"
            isValid = false
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
