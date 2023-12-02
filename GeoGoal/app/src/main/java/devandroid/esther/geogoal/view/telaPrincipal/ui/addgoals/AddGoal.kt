package devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals

import GeocodingService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.map.MapsActivity
import com.google.android.gms.maps.model.LatLng

class AddGoal : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editDescMap: TextView
    private lateinit var mapsActivityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedLocation: LatLng? = null
    private val MAP_REQUEST_CODE = 123

    // Instância de GeocodingService
    private lateinit var geocodingService: GeocodingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("data/tasks")
        editDescMap = findViewById(R.id.editDescMap)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnSaveTask: Button = findViewById(R.id.btnSaveTask)
        val editTextTaskTitle: EditText = findViewById(R.id.editTextTaskTitle)
        val editTextDescription: EditText = findViewById(R.id.editTextDescricao)
        val editMapButton: Button = findViewById(R.id.editMap)

        // Inicializar o GeocodingService com a chave da API do Google Maps
        geocodingService = GeocodingService(getString(R.string.google_maps_key))

        // Configuração do lançador de atividade para o mapa
        mapsActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Obtém as coordenadas do mapa
                val data: Intent? = result.data
                val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
                val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

                // Obter o nome do país usando o GeocodingService
                geocodingService.getCountryName(latitude, longitude) { countryName ->
                    // Atualiza o texto do TextView com as coordenadas e o nome do país
                    editDescMap.text ="País: $countryName\n" +
                            "Latitude: $latitude\n" +
                            "Longitude: $longitude"
                    // Atualiza a variável selectedLocation
                    selectedLocation = LatLng(latitude, longitude)
                }
            }
        }

        btnSaveTask.setOnClickListener {
            val taskTitle = editTextTaskTitle.text.toString()
            val taskDescription = editTextDescription.text.toString()

            if (taskTitle.isNotEmpty() && taskDescription.isNotEmpty() && selectedLocation != null) {
                val task = Task(taskTitle, taskDescription, selectedLocation)

                // Adiciona o novo objeto Task ao Firebase Realtime Database com uma chave única
                val newTaskRef = databaseReference.push()

                newTaskRef.setValue(task).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Meta salva", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("Firebase", "Erro ao salvar a meta: ${task.exception}")
                        Toast.makeText(this, "Erro ao salvar a meta", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos, incluindo a localização no mapa", Toast.LENGTH_SHORT).show()
            }
        }

        editMapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            mapsActivityResultLauncher.launch(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            // Obtém as coordenadas do mapa
            val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            val country = data?.getStringExtra("country")

            // Atualiza o texto do TextView com as coordenadas
            editDescMap.text = "Latitude: $latitude, Longitude: $longitude, País: $country"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
