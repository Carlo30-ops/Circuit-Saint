package com.circuitsaint.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.circuitsaint.databinding.FragmentMapBinding
import com.circuitsaint.data.model.StoreLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Ubicación de la tienda (ejemplo)
    private val storeLocation = StoreLocation(
        name = "Circuit Saint - Tienda Principal",
        address = "Calle Principal 123, Ciudad",
        latitude = 4.6097, // Bogotá, Colombia (ejemplo)
        longitude = -74.0817,
        phone = "+57 1 234 5678",
        hours = "Lun - Vie: 9:00 AM - 7:00 PM"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Configurar el mapa
        val mapFragment = childFragmentManager.findFragmentById(com.circuitsaint.R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Mostrar información de la tienda
        binding.storeName.text = storeLocation.name
        binding.storeAddress.text = storeLocation.address
        storeLocation.phone?.let {
            binding.storePhone.text = "Tel: $it"
        }
        storeLocation.hours?.let {
            binding.storeHours.text = it
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configurar tipo de mapa
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Agregar marcador de la tienda
        val storeLatLng = LatLng(storeLocation.latitude, storeLocation.longitude)
        map.addMarker(
            MarkerOptions()
                .position(storeLatLng)
                .title(storeLocation.name)
                .snippet(storeLocation.address)
        )

        // Mover cámara a la ubicación de la tienda
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLatLng, 15f))
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f)
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap?.isMyLocationEnabled = true
                getCurrentLocation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        googleMap = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

