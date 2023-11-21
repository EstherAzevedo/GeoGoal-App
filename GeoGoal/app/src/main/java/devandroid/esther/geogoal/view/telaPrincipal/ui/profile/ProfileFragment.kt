package devandroid.esther.geogoal.view.telaPrincipal.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var databaseReference: DatabaseReference

    private lateinit var textProfileName: TextView
    private lateinit var textProfileDOB: TextView
    private lateinit var textProfileLocation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Inicialize o Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("data")

        // Inicializar as variáveis
        textProfileName = binding.root.findViewById(R.id.textProfileName)
        textProfileDOB = binding.root.findViewById(R.id.textProfileDOB)
        textProfileLocation = binding.root.findViewById(R.id.textProfileLocation)

        // Configurar o botão de salvar
        binding.btnSaveProfile.setOnClickListener {
            saveProfileData()
        }

        //excluir perfil de usário
        binding.btnExcluirProfile.setOnClickListener {
            deleteProfile()
        }

        loadProfileFromSharedPreferences()
        return binding.root

    }

    // Salvar no Firebase
    private fun saveProfileData() {
        val name = binding.editTextName.text.toString().trim()
        val dob = binding.editTextDOB.text.toString().trim()
        val location = binding.editTextLocation.text.toString().trim()

        if (name.isNotEmpty() && dob.isNotEmpty() && location.isNotEmpty()) {
            // Crie um objeto para representar o perfil
            val profile = Profile(name, dob, location)

            // Salvar no SharedPreferences
            saveProfileToSharedPreferences(profile)

            // Adicione o perfil ao Firebase Realtime Database
            val profileRef = databaseReference.child("profiles").child("usuario_universal")
            profileRef.setValue(profile)

            // Notifique o usuário que o perfil foi salvo
            Toast.makeText(requireContext(), "Perfil salvo", Toast.LENGTH_SHORT).show()

            // Atualize o CardView com as informações do perfil
            updateProfileCard(profile)

        } else {
            // Notifique o usuário se algum campo estiver vazio
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para salvar no SharedPreferences
    private fun saveProfileToSharedPreferences(profile: Profile) {
        val sharedPreferences = requireContext().getSharedPreferences("profile_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("name", profile.name)
        editor.putString("dob", profile.dob)
        editor.putString("location", profile.location)

        editor.apply()
    }

    // Função para carregar dados do SharedPreferences
    private fun loadProfileFromSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("profile_data", Context.MODE_PRIVATE)

        val name = sharedPreferences.getString("name", null)
        val dob = sharedPreferences.getString("dob", null)
        val location = sharedPreferences.getString("location", null)

        if (name != null && dob != null && location != null && name.isNotEmpty() && dob.isNotEmpty() && location.isNotEmpty()) {
            val profile = Profile(name, dob, location)
            updateProfileCard(profile)
        }
    }

    // Função para atualizar o CardView com informações do perfil
    private fun updateProfileCard(profile: Profile) {
        textProfileName.text = "Name: ${profile.name}"
        textProfileDOB.text = "Date of Birth: ${profile.dob}"
        textProfileLocation.text = "Location: ${profile.location}"
    }

    private fun clearProfileFromSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("profile_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.apply()
    }

    private fun deleteProfile() {
        val profileRef = databaseReference.child("profiles").child("usuario_universal")

        // Remover o perfil do Firebase Realtime Database
        profileRef.removeValue()

        // Remover os dados do perfil do SharedPreferences
        clearProfileFromSharedPreferences()

        // Notifique o usuário que o perfil foi excluído
        Toast.makeText(requireContext(), "Perfil excluído", Toast.LENGTH_SHORT).show()

        // Limpe os campos após excluir
        binding.editTextName.text.clear()
        binding.editTextDOB.text.clear()
        binding.editTextLocation.text.clear()
        updateProfileCard(Profile("", "", ""))
    }
}
