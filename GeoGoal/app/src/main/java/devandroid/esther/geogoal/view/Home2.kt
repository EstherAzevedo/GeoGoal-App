package devandroid.esther.geogoal.view.telaPrincipal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.databinding.ActivityHome2Binding
import devandroid.esther.geogoal.view.telaPrincipal.ui.addgoals.AddGoal

class Home2 : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHome2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflar layout
        binding = ActivityHome2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //configuração da toolbar
        setSupportActionBar(binding.appBarHome2.toolbar)

        //configuração do menu Drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home2)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_gallery || destination.id == R.id.nav_slideshow || destination.id == R.id.nav_history || destination.id == R.id.nav_log_out) {
                // Ocultar o botão inputAdd nos fragments Gallery, Slideshow, History, LogOut
                binding.appBarHome2.inputAdd.visibility = View.GONE
            } else {
                // Mostrar o botão inputAdd nos outros fragments
                binding.appBarHome2.inputAdd.visibility = View.VISIBLE
            }
        }

        // Adicionar evento de clique ao botão inputAdd
        binding.appBarHome2.inputAdd.setOnClickListener {
            // Criar um Intent para iniciar a AddGoalActivity
            val intent = Intent(this, AddGoal::class.java)
            // Iniciar a atividade usando o Intent
            startActivity(intent)
        }

    //classe é usada para especificar quais destinos (fragments, activities) são considerados níveis de topo
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_history, R.id.nav_log_out
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    //configuração p/ btn voltar
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}