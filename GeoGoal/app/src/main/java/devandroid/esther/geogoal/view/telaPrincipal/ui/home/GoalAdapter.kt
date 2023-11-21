package devandroid.esther.geogoal.view.telaPrincipal.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.Task

class GoalsAdapter(private val clickListener: GoalItemClickListener) :
    RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder>() {

    private var goalsList: List<Task> = listOf()

    //métodos para manipular eventos de clique nos itens da lista.
    interface GoalItemClickListener {
        fun onDeleteClick(position: Int)
        fun onUpdateClick(position: Int)
        fun onConcluidoClick(position: Int)
        abstract fun getGoalId(dataSnapshot: Task): String?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalsViewHolder(itemView, clickListener)
    }

    //responsável por associar os dados a um item específico dentro do RecyclerView
    override fun onBindViewHolder(holder: GoalsViewHolder, position: Int) {
        val currentGoal = goalsList[position]
        holder.bind(currentGoal)
    }

    override fun getItemCount(): Int {
        return goalsList.size
    }

    fun updateGoals(goals: List<Task>) {
        goalsList = goals
        notifyDataSetChanged()
    }

    //retorna o objeto Task localizado em uma posição específica da lista de metas (goalsList).
    fun getGoalAt(position: Int): Task {
        return goalsList[position]
    }

    class GoalsViewHolder(itemView: View, private val clickListener: GoalItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewGoalTitle)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewGoalDescription)
        private val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnConcluido: Button = itemView.findViewById(R.id.btnConcluido)

        fun bind(goal: Task) {
            textViewTitle.text = goal.title
            textViewDescription.text = goal.description

            btnDelete.setOnClickListener {
                clickListener.onDeleteClick(adapterPosition)
            }

            btnUpdate.setOnClickListener {
                clickListener.onUpdateClick(adapterPosition)
            }
            btnConcluido.setOnClickListener {
                clickListener.onConcluidoClick(adapterPosition)
            }
        }
        }
    }

