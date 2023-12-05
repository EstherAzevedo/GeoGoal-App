package devandroid.esther.geogoal.view.telaPrincipal.ui.history
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.Task
import devandroid.esther.geogoal.view.telaPrincipal.ui.gallery.GalleryActivity

class GoalsCompletedAdapter(private val completedGoals: List<Task>) :
    RecyclerView.Adapter<GoalsCompletedAdapter.CompletedGoalsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedGoalsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_completed_goal, parent, false)
        return CompletedGoalsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompletedGoalsViewHolder, position: Int) {
        val currentGoal = completedGoals[position]
        holder.bind(currentGoal)

        // Adicionar OnClickListener para o botão dentro de cada item
        holder.itemView.findViewById<Button>(R.id.btnPhotoGallery).setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, GalleryActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return completedGoals.size
    }

    class CompletedGoalsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewCompletedGoalTitle)
        private val textViewDescription: TextView =
            itemView.findViewById(R.id.textViewCompletedGoalDescription)

        fun bind(goal: Task) {
            textViewTitle.text = goal.title
            textViewDescription.text = goal.description
        }
    }
}