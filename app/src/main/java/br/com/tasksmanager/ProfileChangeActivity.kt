package br.com.tasksmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.tasksmanager.databinding.ActivityProfileChangeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileChangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileChangeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pagePasswordButton.setOnClickListener{
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val documentRef = db.collection("usuarios").document(uid)

                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val data = documentSnapshot.data
                            val email = data?.get("email") as String?
                            //enviando email para restaurar senha
                            firebaseAuth.sendPasswordResetEmail(email.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val snackbar = Snackbar.make(binding.root,"Um e-mail de reset de senha foi enviado para: "+ email,Snackbar.LENGTH_LONG)
                                        snackbar.duration = 3000
                                        snackbar.show()
                                    } else {
                                        val snackbar = Snackbar.make(binding.root,"Erro ao enviar reset de senha para o e-mail: "+ email,Snackbar.LENGTH_LONG)
                                        snackbar.duration = 3000
                                        snackbar.show()
                                    }
                                }


                        } else {
                            val snackbar = Snackbar.make(binding.root,"Erro ao enviar reset de senha",Snackbar.LENGTH_LONG)
                            snackbar.duration = 3000
                            snackbar.show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        val snackbar = Snackbar.make(binding.root,"Erro ao pegar dados para envio de reset de senha",Snackbar.LENGTH_LONG)
                        snackbar.duration = 3000
                        snackbar.show()
                    }
            }
        }
        binding.alterUserButton.setOnClickListener{
            val nome = binding.txtUser.text.toString()
            val user = firebaseAuth.currentUser
            val uid = user?.uid
            if(nome.isNotEmpty()){
                db.collection("usuarios").document(uid.toString())
                    .update("nome",nome).addOnSuccessListener {
                        val snackbar = Snackbar.make(binding.root,"Nome atualizado com sucesso!",Snackbar.LENGTH_LONG)
                        snackbar.duration = 3000
                        snackbar.show()
                    }.addOnFailureListener {

                    }
            }
            else{
                val snackbar = Snackbar.make(binding.root,"Campo nome não deve estar vazio!",Snackbar.LENGTH_LONG)
                snackbar.duration = 3000
                snackbar.show()
            }
        }
    }
}