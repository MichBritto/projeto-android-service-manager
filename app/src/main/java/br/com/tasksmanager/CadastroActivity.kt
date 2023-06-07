package br.com.tasksmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.tasksmanager.databinding.ActivityCadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtIrPLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnCadastrar.setOnClickListener{
            val nome = binding.txtNome.text.toString()
            val email = binding.txtEmail.text.toString()
            val senha = binding.txtSenha.text.toString()
            val confirmarSenhar = binding.txtConfirmarSenha.text.toString()

            if(nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmarSenhar.isNotEmpty()){
                if(senha == confirmarSenhar) {
                    firebaseAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val uid = user?.uid
                                //adicionar usuario ao database
                                // Create a new user with a name and email
                                val userData = hashMapOf(
                                    "nome" to nome,
                                    "email" to email,
                                )
                                db.collection("usuarios").document(uid.toString()).set(userData).addOnSuccessListener{documentReference ->
                                    val snackbar = Snackbar.make(binding.root,"Conta criada com sucesso, você será redirecionado para a área de login.",Snackbar.LENGTH_LONG)
                                    snackbar.duration = 3000
                                    snackbar.show()
                                    //chamar a tela de login somente apos 3 sec
                                    val handler = Handler()
                                    handler.postDelayed({
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }, 3000)
                                }.addOnFailureListener{
                                    val errorMessage = when (it){
                                        is FirebaseError -> "Erro interno com Firebase, tente novamente ou contacte o suporte."
                                        else -> "Erro ao registrar conta"
                                    }
                                    Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show()
                                }
                            }
                        }.addOnFailureListener{
                            val errorMessage = when(it){
                                is FirebaseAuthWeakPasswordException -> "Senha deve conter pelo menos 6 caractéres!"
                                is FirebaseAuthInvalidCredentialsException -> "Digite um email válido!"
                                is FirebaseAuthUserCollisionException -> "E-mail já está em uso!"
                                else -> "Não foi possível cadastrar sua conta"
                            }
                            val snackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                            snackbar.duration = 3000
                            snackbar.show()
                        }
                }
                else{
                    val snackbar = Snackbar.make(binding.root, "As senhas não correspondem", Snackbar.LENGTH_LONG)
                    snackbar.duration = 3000
                    snackbar.show()
                }
            }
            else{
                val snackbar = Snackbar.make(binding.root, "Existe campos que não foram preenchidos", Snackbar.LENGTH_LONG)
                snackbar.duration = 3000
                snackbar.show()
            }
        }
    }
}