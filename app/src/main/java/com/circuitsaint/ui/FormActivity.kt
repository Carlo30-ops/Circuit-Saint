package com.circuitsaint.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.circuitsaint.databinding.ActivityFormBinding

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var qrCode: String = ""

    companion object {
        private const val EXTRA_QR_CODE = "extra_qr_code"

        fun newIntent(context: Context, qrCode: String = ""): Intent {
            return Intent(context, FormActivity::class.java).apply {
                putExtra(EXTRA_QR_CODE, qrCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Formulario de Contacto"

        qrCode = intent.getStringExtra(EXTRA_QR_CODE) ?: ""

        if (qrCode.isNotEmpty()) {
            binding.qrCodeText.text = "Código QR: $qrCode"
            binding.qrCodeText.visibility = android.view.View.VISIBLE
        } else {
            binding.qrCodeText.visibility = android.view.View.GONE
        }

        setupViews()
    }

    private fun setupViews() {
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.editName.text.toString().trim().isEmpty()) {
            binding.editName.error = "El nombre es requerido"
            isValid = false
        }

        if (binding.editEmail.text.toString().trim().isEmpty()) {
            binding.editEmail.error = "El email es requerido"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
                binding.editEmail.text.toString()
            ).matches()
        ) {
            binding.editEmail.error = "Email inválido"
            isValid = false
        }

        if (binding.editPhone.text.toString().trim().isEmpty()) {
            binding.editPhone.error = "El teléfono es requerido"
            isValid = false
        }

        if (binding.editMessage.text.toString().trim().isEmpty()) {
            binding.editMessage.error = "El mensaje es requerido"
            isValid = false
        }

        return isValid
    }

    private fun submitForm() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()
        val message = binding.editMessage.text.toString().trim()

        // Aquí se enviaría la información a un servidor
        // Por ahora, solo mostramos un mensaje
        Toast.makeText(
            this,
            "Formulario enviado exitosamente\nNombre: $name\nEmail: $email",
            Toast.LENGTH_LONG
        ).show()

        // Limpiar formulario
        binding.editName.text?.clear()
        binding.editEmail.text?.clear()
        binding.editPhone.text?.clear()
        binding.editMessage.text?.clear()

        // Cerrar después de un momento
        binding.root.postDelayed({
            finish()
        }, 2000)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

