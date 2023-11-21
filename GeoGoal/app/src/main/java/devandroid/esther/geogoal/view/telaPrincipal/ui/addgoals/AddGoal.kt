package devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.map.MapsActivity

class AddGoal : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        // Inicialização das instâncias do Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("data/tasks")

        //configuração da toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Ref. elementos da UI
        val btnSaveTask: Button = findViewById(R.id.btnSaveTask)
        val editTextTaskTitle: EditText = findViewById(R.id.editTextTaskTitle)
        val editTextDescription: EditText = findViewById(R.id.editTextDescricao)
        val editMapTextView: TextView = findViewById(R.id.editMap)

        btnSaveTask.setOnClickListener {
            val taskTitle = editTextTaskTitle.text.toString()
            val taskDescription = editTextDescription.text.toString()

            if (taskTitle.isNotEmpty() && taskDescription.isNotEmpty()) {
                // Cria um novo objeto Task
                val task = Task(taskTitle, taskDescription)

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
                Toast.makeText(this, "Título ou descrição da meta está vazio", Toast.LENGTH_SHORT).show()
            }
        }

        editMapTextView.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
