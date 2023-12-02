package devandroid.esther.geogoal.view.telaPrincipal.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.map.MapsActivity
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.Task
import devandroid.esther.geogoal.view.telaPrincipal.ui.history.HistoryFragment
import devandroid.esther.geogoal.view.telaPrincipal.ui.updategoals.UpdateGoals

class HomeFragment : Fragment(), GoalsAdapter.GoalItemClickListener {

    private lateinit var recyclerViewGoals: RecyclerView
    private lateinit var goalsAdapter: GoalsAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //configuração de um RecyclerView
        recyclerViewGoals = view.findViewById(R.id.recyclerViewGoals)
        goalsAdapter = GoalsAdapter(this)
        recyclerViewGoals.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewGoals.adapter = goalsAdapter

        // Inicializa DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("data/tasks")

        // Adicionando um listener para receber atualizações em tempo real
        databaseReference.addValueEventListener(object : ValueEventListener {
            //quando há alteração e o snapshot contém os dados atuais no nó referenciado
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Mudança nos dados detectada.")
                val goalsList = mutableListOf<Task>()

                for (taskSnapshot in snapshot.children) {
                    val title = taskSnapshot.child("title").getValue(String::class.java) ?: ""
                    val description = taskSnapshot.child("description").getValue(String::class.java) ?: ""

                    //essa instância de task é criada e add a goalslist
                    val task = Task(title, description)
                    goalsList.add(task)
                }
                //mostrar goals na tela
                displayGoals(goalsList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erro ao ler metas do banco de dados: ${error.message}")
            }
        })
        return view
    }

    override fun onDeleteClick(position: Int) {
        Log.d("Firebase", "onDeleteClick chamado na posição: $position")
        val selectedGoal = goalsAdapter.getGoalAt(position)
        deleteGoal(selectedGoal)
    }

    private fun displayGoals(goals: List<Task>) {
        goalsAdapter.updateGoals(goals)
        goalsAdapter.notifyDataSetChanged()
    }

    private fun deleteGoal(selectedGoal: Task?) {
        // Certifique-se de que selectedGoal não seja nulo antes de prosseguir
        selectedGoal?.let {
            val title = selectedGoal.title
            if (!title.isNullOrBlank()) {
                val query = databaseReference.orderByChild("title").equalTo(title)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            snapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Log.d("Firebase", "Meta excluída com sucesso!")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firebase", "Erro ao excluir a meta: ${e.message}")
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Firebase", "Erro ao buscar a meta pelo título: ${databaseError.message}")
                    }
                })
            } else {
                Log.e("Firebase", "Título da meta é nulo ou vazio.")
            }
        }
    }


    // Implementação necessária
    override fun getGoalId(dataSnapshot: Task): String? {
        // Neste caso, como o ID é a chave única gerada automaticamente pelo Firebase, retornamos a chave
        return dataSnapshot.title
    }
    override fun onUpdateClick(position: Int) {
        val selectedGoal = goalsAdapter.getGoalAt(position)

        // Criar a Intent para iniciar a UpdateGoals Activity
        val intent = Intent(requireContext(), UpdateGoals::class.java)

        // Passar os dados necessários para a UpdateGoals Activity (se necessário)
        intent.putExtra("goalTitle", selectedGoal.title)
        intent.putExtra("goalDescription", selectedGoal.description)

        // Adicionar a LocationData completa
        selectedGoal.location?.let {
            intent.putExtra("locationData", it)
        }

        // Iniciar a UpdateGoals Activity
        startActivity(intent)
    }

    //btnconcluido
    override fun onConcluidoClick(position: Int) {
        val selectedGoal = goalsAdapter.getGoalAt(position)
        deleteGoal(selectedGoal)

        addCompletedGoalToHistory(selectedGoal)

        Toast.makeText(requireContext(), "Meta concluída", Toast.LENGTH_SHORT).show()
    }
    private fun addCompletedGoalToHistory(completedGoal: Task) {
        val historyReference = FirebaseDatabase.getInstance().getReference("data/completed_tasks")
        // Crie uma nova chave única para a meta concluída
        val newHistoryEntry = historyReference.push()
        // Defina os valores da meta concluída
        newHistoryEntry.child("title").setValue(completedGoal.title)
        newHistoryEntry.child("description").setValue(completedGoal.description)
        // Outros dados que você deseja armazenar...

        // Adicione um ouvinte para verificar se a operação foi bem-sucedida
        newHistoryEntry.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Firebase", "Meta concluída adicionada ao histórico com sucesso!")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Erro ao adicionar a meta concluída ao histórico: ${databaseError.message}")
            }
        })
    }
}
