package com.circuitsaint.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.circuitsaint.databinding.FragmentQrScannerBinding
import com.circuitsaint.util.Config
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrScannerFragment : Fragment() {
    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!
    private var isScanning = false
    private var cameraProvider: ProcessCameraProvider? = null
    private var analysisExecutor: ExecutorService? = null

    // Activity Result API para permisos de cámara
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startScanner()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analysisExecutor = Executors.newSingleThreadExecutor()

        binding.startScanButton.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanner()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permiso de Cámara Necesario")
            .setMessage("Necesitamos acceso a la cámara para escanear códigos QR. Por favor, permite el acceso en la configuración.")
            .setPositiveButton("Solicitar Permiso") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permiso Denegado")
            .setMessage("No se puede escanear QR sin permiso de cámara. Por favor, habilita el permiso en Configuración > Aplicaciones > Circuit Saint > Permisos.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun startScanner() {
        if (isScanning) return
        isScanning = true

        binding.initialStateLayout.visibility = View.GONE
        binding.resultLayout.visibility = View.GONE
        binding.scanningLayout.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)

            analysis.setAnalyzer(analysisExecutor!!) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null && isScanning) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (!isScanning) return@addOnSuccessListener
                            val first = barcodes.firstOrNull()
                            if (first != null) {
                                isScanning = false
                                vibrateOnce()
                                showResult(first.rawValue ?: "")
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis
                )
            } catch (_: Exception) {
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun showResult(text: String) {
        binding.scanningLayout.visibility = View.GONE
        binding.initialStateLayout.visibility = View.GONE
        binding.resultLayout.visibility = View.VISIBLE
        binding.scannedDataTextView.text = text
    }

    private fun vibrateOnce() {
        val v = requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        v?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // deprecated but safe fallback
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isScanning = false
        cameraProvider?.unbindAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isScanning = false
        cameraProvider?.unbindAll()
        analysisExecutor?.shutdown()
        _binding = null
    }
}
