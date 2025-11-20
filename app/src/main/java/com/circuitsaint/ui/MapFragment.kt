package com.circuitsaint.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.circuitsaint.databinding.FragmentMapBinding
import com.circuitsaint.util.Config
import com.circuitsaint.util.PerformanceOptimizer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // optimize memory for map
        PerformanceOptimizer.configureMapForLowMemory(requireContext())

        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(binding.map.id, it)
                    .commitNowAllowingStateLoss()
            }
        mapFragment.getMapAsync(this)

        binding.btnShareLocation.setOnClickListener {
            // Open navigation via geo intent
            val storeLocation = Config.STORE_LOCATION
            val gmmIntentUri = android.net.Uri.parse("google.navigation:q=${storeLocation.latitude},${storeLocation.longitude}")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // fallback to generic intent
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=${storeLocation.latitude},${storeLocation.longitude}")))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Minimal UI settings
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMapToolbarEnabled = false

        // Add marker and animate camera
        val storeLocation = Config.STORE_LOCATION
        map?.addMarker(MarkerOptions().position(storeLocation).title(Config.STORE_NAME))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15f))

        // Location permission optional: only enable if user granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release map reference
        map = null
        _binding = null
    }
}