package devandroid.esther.geogoal.view.telaPrincipal.ui.logout

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import devandroid.esther.geogoal.databinding.FragmentLogOutBinding
import devandroid.esther.geogoal.view.formlogin.FormLogin

class LogOutFragment : Fragment() {

    private lateinit var binding: FragmentLogOutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), FormLogin::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
