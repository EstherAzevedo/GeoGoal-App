package devandroid.esther.geogoal.view.telaPrincipal.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.Task

class HistoryFragment : Fragment() {
    private lateinit var recyclerViewCompletedGoals: RecyclerView
    private lateinit var completedGoalsAdapter: GoalsCompletedAdapter
    private val completedGoalsList = mutableListOf<Task>()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialize a referência do banco de dados para "data/completed_tasks"
        databaseReference = FirebaseDatabase.getInstance().getReference("data/completed_tasks")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerViewCompletedGoals = view.findViewById(R.id.recyclerViewCompletedGoals)
        completedGoalsAdapter = GoalsCompletedAdapter(completedGoalsList)
        recyclerViewCompletedGoals.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCompletedGoals.adapter = completedGoalsAdapter

        // Adicionando um listener para receber atualizações em tempo real
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpar a lista antes de adicionar novos dados
                completedGoalsList.clear()

                for (taskSnapshot in snapshot.children) {
                    val title = taskSnapshot.child("title").getValue(String::class.java) ?: ""
                    val description = taskSnapshot.child("description").getValue(String::class.java) ?: ""

                    val task = Task(title, description)
                    completedGoalsList.add(task)
                }

                // Notifique o adapter sobre a mudança nos dados
                completedGoalsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros de leitura do banco de dados, se necessário
            }
        })

        return view
    }

    //fornecer uma maneira consistente e controlada de obter instâncias de uma classe
    companion object {
        fun getInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }
}