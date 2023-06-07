package br.com.tasksmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.tasksmanager.databinding.ActivityAddOrderBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class AddOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddOrderBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //variaveis para configuração de nó de realtime database, gerar id e conversão de data em modelo brasileiro
        val currentDate = Date()
        binding.btnRegistrar.setOnClickListener {
            val nomeOrdem = binding.txtNome.text.toString()
            val descricao = binding.txtDescricao.text.toString()
            if(nomeOrdem.isNotEmpty() && descricao.isNotEmpty()){
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                //objeto
                val orderData = mapOf(
                    "titulo" to nomeOrdem,
                    "descricao" to descricao,
                    "status" to "PENDENTE",
                    "data" to currentDate,
                    "userUid" to uid
                )

                db.collection("ordens-servico").add(orderData).addOnSuccessListener{documentReference ->
                    val snackbar = Snackbar.make(binding.root,"Ordem de serviço registrada com sucesso, acompanhe o andamento em: 'Suas Ordens'.",Snackbar.LENGTH_LONG)
                    snackbar.duration = 3000
                    snackbar.show()
                    //chamar a tela de login somente apos 3 sec
                    val handler = Handler()
                    handler.postDelayed({
                        val intent = Intent(this, MenuActivity::class.java)
                        startActivity(intent)
                    }, 3000)
                }.addOnFailureListener{
                    val errorMessage = when (it){
                        is FirebaseError -> "Erro interno com Firebase, tente novamente ou contacte o suporte."
                        else -> "Erro ao registrar ordem"
                    }
                    val snackbar = Snackbar.make(binding.root,errorMessage,Snackbar.LENGTH_LONG)
                    snackbar.duration = 3000
                    snackbar.show()
                }
            }
            else {
                val errorMessage = "Existem campos que precisam ser preenchidos."
                val snackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                snackbar.duration = 3000
                snackbar.show()
            }
        }

    }
}