package devandroid.esther.geogoal.view.map

import android.content.Intent // Adicione esta importação
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configuração dos controles de câmera
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true

        // Exemplo: centrar o mapa em Quixadá, Brasil
        val quixada = LatLng(-4.9967376, -39.0829881)
        mMap.addMarker(MarkerOptions().position(quixada).title("Marker in Quixadá"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quixada, 12.0f)) // Zoom 12.0f

        // Adiciona um marcador ao tocar no mapa
        mMap.setOnMapClickListener { latLng ->
            mMap.addMarker(MarkerOptions().position(latLng).title("Custom Marker"))
            selectedLocation = latLng

            // Retorna as coordenadas à tela AddGoal
            val resultIntent = Intent()
            resultIntent.putExtra("latitude", latLng.latitude)
            resultIntent.putExtra("longitude", latLng.longitude)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    // Método público para obter as coordenadas selecionadas
    fun getSelectedLocation(): LatLng? {
        return selectedLocation
    }
}
