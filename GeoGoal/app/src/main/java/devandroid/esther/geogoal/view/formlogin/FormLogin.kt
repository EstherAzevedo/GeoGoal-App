package devandroid.esther.geogoal.view.formlogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import devandroid.esther.geogoal.databinding.ActivityFormLoginBinding
import devandroid.esther.geogoal.view.formcadastro.FormCadastro
import devandroid.esther.geogoal.view.telaPrincipal.Home2

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { view ->
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { autenticacao ->
                    if (autenticacao.isSuccessful) {
                        navegarTelaPrincipal()
                    }
                }.addOnFailureListener {
                    val snackbar = Snackbar.make(view, "Erro ao fazer login!", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }

        binding.textoIrparaCadastro.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        binding.btnEsqueciMinhaSenha.setOnClickListener {
            val email = binding.editEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu e-mail acima", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "E-mail de redefinição de senha enviado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Falha ao enviar e-mail de redefinição de senha", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun navegarTelaPrincipal() {
        val intent = Intent(this, Home2::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance()
        val user = usuarioAtual.currentUser

        if (user != null) {
            val userId = user.uid
            navegarTelaPrincipal()
        }
    }
}
