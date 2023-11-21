package devandroid.esther.geogoal.view.telaPrincipal.ui.updategoals

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
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.Task

class UpdateGoals : AppCompatActivity() {
    private lateinit var editTextTaskTitle: EditText
    private lateinit var editTextDescricao: EditText
    private lateinit var btnSaveTask: Button
    private lateinit var editMapTextView: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_goals)

        // Inicializar a referência ao banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference("data")

        //configuração da toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obter dados passados da Intent
        val goalTitle = intent.getStringExtra("goalTitle") ?: ""
        val goalDescription = intent.getStringExtra("goalDescription") ?: ""

        // Inicializar os campos de edição
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle)
        editTextDescricao = findViewById(R.id.editTextDescricao)
        btnSaveTask = findViewById(R.id.btnSaveTask)
        editMapTextView = findViewById(R.id.editMap)

        // Preencher os campos de edição com os dados da meta
        editTextTaskTitle.setText(goalTitle)
        editTextDescricao.setText(goalDescription)

        // Ir para o mapa
        editMapTextView.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        // Atualizar a meta no clique do botão
        btnSaveTask.setOnClickListener {
            val updatedTitle = editTextTaskTitle.text.toString()
            val updatedDescription = editTextDescricao.text.toString()

            // Certifique-se de que o título antigo não está vazio
            if (goalTitle.isNotEmpty()) {
                // Encontrar a entrada correspondente usando o título antigo
                val query = databaseReference.child("tasks").orderByChild("title").equalTo(goalTitle)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            // Atualizar os valores na entrada correspondente
                            snapshot.ref.child("title").setValue(updatedTitle)
                            snapshot.ref.child("description").setValue(updatedDescription)

                            Toast.makeText(applicationContext, "Meta Atualizada", Toast.LENGTH_SHORT).show()
                            finish()
                            Log.d("Firebase", "Meta atualizada com sucesso!")
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Firebase", "Erro ao buscar a meta pelo título: ${databaseError.message}")
                        Toast.makeText(applicationContext, "Erro ao atualizar a meta", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    //voltar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
