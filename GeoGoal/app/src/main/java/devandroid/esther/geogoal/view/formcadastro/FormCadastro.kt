package devandroid.esther.geogoal.view.formcadastro

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import devandroid.esther.geogoal.R
import devandroid.esther.geogoal.databinding.ActivityFormCadastroBinding

class FormCadastro : AppCompatActivity() {
    private lateinit var binding: ActivityFormCadastroBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCadastrar.setOnClickListener {view ->
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if(email.isEmpty() || senha.isEmpty()){
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }else{
                auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener{ cadastro ->
                    if(cadastro.isSuccessful){
                        val snackbar = Snackbar.make(view, "Sucesso ao cadastrar usuário", Snackbar.LENGTH_LONG)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                    }
                }.addOnFailureListener{ exception ->
                    val mensagemErro = when(exception) {
                        is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
                        is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido!"
                        is FirebaseAuthUserCollisionException -> "Esta conta já foi cadastrada!"
                        is FirebaseNetworkException -> "Sem conexão com a internet"
                        else -> {"Erro ao cadastrar usuário"}
                    }
                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }
    }
}