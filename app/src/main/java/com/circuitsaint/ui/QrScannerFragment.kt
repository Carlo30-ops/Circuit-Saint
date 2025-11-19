package com.circuitsaint.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.circuitsaint.databinding.FragmentQrScannerBinding
import com.circuitsaint.util.PerformanceOptimizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class QrScannerFragment : Fragment() {
    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!
    private var isScanning = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        PerformanceOptimizer.optimizeCameraForBattery(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            startScanner()
        }
    }

    private fun startScanner() {
        if (isScanning) return
        isScanning = true

        binding.barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let { r ->
                    // debounce multiple reads
                    if (!isScanning) return
                    isScanning = false

                    // feedback
                    vibrateOnce()
                    val beep = RingtoneManager.getRingtone(requireContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    beep.play()

                    handleScanResult(r.text)
                    // stop preview briefly
                    binding.barcodeView.pause()
                    binding.root.postDelayed({
                        // resume scanning after 1.5s
                        binding.barcodeView.resume()
                        isScanning = true
                    }, 1500)
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    private fun handleScanResult(text: String) {
        // JSON detection
        val trimmed = text.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            // could parse JSON and act accordingly
            if (trimmed.contains("\"form\"") || trimmed.contains("circuit_saint")) {
                // open form
                val i = Intent(requireContext(), FormActivity::class.java)
                i.putExtra(FormActivity.EXTRA_QR_DATA, trimmed)
                startActivity(i)
                return
            }
        }
        // URL detection
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            val i = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(trimmed))
            startActivity(i)
            return
        }
        // Plain text: show in dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Contenido QR")
            .setMessage(trimmed)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun vibrateOnce() {
        val v = requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        v?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
        binding.barcodeView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isScanning = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanner()
        } else {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setMessage("Necesitamos permiso a la cámara para escanear QR.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}
