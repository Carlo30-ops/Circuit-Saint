package com.circuitsaint.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.circuitsaint.databinding.FragmentQrScannerBinding
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class QrScannerFragment : Fragment() {

    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!

    private var barcodeView: DecoratedBarcodeView? = null

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            if (result?.text != null) {
                // Detener el escaneo
                barcodeView?.pause()
                
                // Procesar el resultado del QR
                handleQrResult(result.text)
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            // Opcional: mostrar puntos de resultado
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = binding.barcodeScanner

        // Configurar el escáner
        barcodeView?.decodeContinuous(callback)

        // Verificar permisos de cámara
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startScanning()
        }

        binding.btnOpenForm.setOnClickListener {
            // Abrir formulario directamente
            openFormActivity()
        }
    }

    private fun startScanning() {
        barcodeView?.resume()
    }

    private fun handleQrResult(qrText: String) {
        // El QR puede contener un URL o un código que vincula al formulario
        when {
            qrText.startsWith("http://") || qrText.startsWith("https://") -> {
                // Si es un URL, abrir el formulario
                openFormActivity()
            }
            qrText.startsWith("FORM:") -> {
                // Si tiene prefijo FORM:, extraer datos
                val formData = qrText.removePrefix("FORM:")
                openFormActivity(formData)
            }
            else -> {
                // Cualquier otro texto, abrir formulario con el texto como código
                openFormActivity(qrText)
            }
        }
    }

    private fun openFormActivity(code: String = "") {
        val intent = FormActivity.newIntent(requireContext(), code)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Se necesita permiso de cámara para escanear QR",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            barcodeView?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        barcodeView?.pause()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
    }
}

