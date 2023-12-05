package devandroid.esther.geogoal.view.telaPrincipal.ui.updategoals

import GeocodingService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.map.MapsActivity

class UpdateGoals : AppCompatActivity() {
    private lateinit var editTextTaskTitle: EditText
    private lateinit var editTextDescricao: EditText
    private lateinit var btnSaveTask: Button
    private lateinit var editMapTextView: TextView
    private lateinit var databaseReference: DatabaseReference
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val MAP_UPDATE_REQUEST_CODE = 456

    // Instância de GeocodingService
    private lateinit var geocodingService: GeocodingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_goals)

        // Inicializar a referência ao banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference("data")

        // Configuração da toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicializar os campos de edição
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle)
        editTextDescricao = findViewById(R.id.editTextDescricao)
        btnSaveTask = findViewById(R.id.btnSaveTask)
        editMapTextView = findViewById(R.id.editDescMap)
        val editMapButton: Button = findViewById(R.id.editMap)

        // Inicializar o GeocodingService com a chave da API do Google Maps
        geocodingService = GeocodingService(getString(R.string.google_maps_key))

        // Obter dados passados da Intent
        val goalTitle = intent.getStringExtra("goalTitle") ?: ""
        val goalDescription = intent.getStringExtra("goalDescription") ?: ""

        // Preencher os campos de edição com os dados da meta
        editTextTaskTitle.setText(goalTitle)
        editTextDescricao.setText(goalDescription)

        // Carregar as coordenadas do Firebase
        loadCoordinatesFromFirebase(goalTitle)

        // Ir para o mapa para atualização
        editMapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivityForResult(intent, MAP_UPDATE_REQUEST_CODE)
        }

        // Atualizar a meta no clique do botão
        btnSaveTask.setOnClickListener {
            val updatedTitle = editTextTaskTitle.text.toString()
            val updatedDescription = editTextDescricao.text.toString()

            // Certifique-se de que o título antigo não está vazio
            if (goalTitle.isNotEmpty()) {
                // Encontrar a entrada correspondente usando o título antigo
                val query =
                    databaseReference.child("tasks").orderByChild("title").equalTo(goalTitle)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            // Atualizar os valores na entrada correspondente
                            snapshot.ref.child("title").setValue(updatedTitle)
                            snapshot.ref.child("description").setValue(updatedDescription)
                            snapshot.ref.child("location").child("latitude").setValue(latitude)
                            snapshot.ref.child("location").child("longitude").setValue(longitude)

                            Toast.makeText(
                                applicationContext,
                                "Meta Atualizada",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                            Log.d("Firebase", "Meta atualizada com sucesso!")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(
                            "Firebase",
                            "Erro ao buscar a meta pelo título: ${databaseError.message}"
                        )
                        Toast.makeText(
                            applicationContext,
                            "Erro ao atualizar a meta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    // Método para carregar as coordenadas do Firebase
    private fun loadCoordinatesFromFirebase(goalTitle: String) {
        val query = databaseReference.child("tasks").orderByChild("title").equalTo(goalTitle)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // Obter as coordenadas do Firebase
                    val locationSnapshot = snapshot.child("location")
                    latitude = locationSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                    longitude = locationSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0

                    // Atualizar o texto do TextView com as coordenadas
                    updateMapTextView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Erro ao buscar as coordenadas: ${databaseError.message}")
            }
        })
    }

    // Método para atualizar o texto de latitude e longitude
    private fun updateMapTextView() {
        // Obter o nome do país e da cidade usando o GeocodingService
        geocodingService.getCountryAndCityName(latitude, longitude) { countryName, cityName ->
            // Atualizar o TextView com as coordenadas, nome do país e nome da cidade
            editMapTextView.text = "País: $countryName\nCidade: $cityName\n" +
                    "Latitude: $latitude\nLongitude: $longitude"
        }
    }

    // Método para processar o resultado da atividade do mapa
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MAP_UPDATE_REQUEST_CODE -> {
                // Obtém as novas coordenadas do mapa de atualização
                val newLatitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
                val newLongitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

                // Obtém o nome do país e da cidade usando o GeocodingService
                geocodingService.getCountryAndCityName(newLatitude, newLongitude) { countryName, cityName ->
                    // Atualiza o texto do TextView com as novas coordenadas, nome do país e nome da cidade
                    editMapTextView.text = "País: $countryName\nCidade: $cityName\n" +
                            "Latitude: $newLatitude\nLongitude: $newLongitude"
                }

                // Atualiza as variáveis de latitude e longitude
                latitude = newLatitude
                longitude = newLongitude
            }
        }
    }

    // Voltar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val MAP_UPDATE_REQUEST_CODE = 456
    }
}
