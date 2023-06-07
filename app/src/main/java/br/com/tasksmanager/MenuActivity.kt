package br.com.tasksmanager

import adapter.OrderAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.tasksmanager.AddOrderActivity
import br.com.tasksmanager.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import model.Order
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.widget.EditText
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MenuActivity : AppCompatActivity(), OrderAdapter.OnOrderClickListener {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var adapter: OrderAdapter
    private var selected_item: Order? = null
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userInfo = currentUser?.displayName

            // Imprimir todas as informações do usuário
            println("Informacoes do usuario:")
            println(userInfo)
        } else {
            println("Nenhum usuário autenticado")
        }

        val listaOrdensServico = mutableListOf<Order>()

        runBlocking {
            getOrdensServico(listaOrdensServico)

            // Agora você pode acessar os itens adicionados na listaOrdensServico
            listaOrdensServico.forEach { ordemServico ->
                println(ordemServico.getTitulo())
                println(ordemServico.getDescricao())
                println(ordemServico.getStatus())
            }
        }

        adapter = OrderAdapter(this, listaOrdensServico, this)
        binding.ordemServico.layoutManager = LinearLayoutManager(this)
        binding.ordemServico.adapter = adapter

        binding.addOrderButton.setOnClickListener {

            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }

        binding.editarPerfilButton.setOnClickListener{
            //val intent = Intent(this, ProfileChangeActivity::class.java)
            //startActivity(intent)
            showInputDialog()
        }

        binding.btnDetailOrder.setOnClickListener {
            if (selected_item != null) {
                val intent = Intent(this, OrderDetailActivity::class.java)
                startActivity(intent)
            }
        }
        binding.logOut.setOnClickListener{
            auth.signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        /*binding.btnShare.setOnClickListener{
            if (selected_item != null){
                showInputDialog()
            }
            else{
                Toast.makeText(this, "Selecione uma ordem", Toast.LENGTH_LONG).show()
            }

        }*/


    }

    override fun onOrderClick(order: Order) {
        selected_item = order
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Insira um numero de telefone")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            val phoneNumber = input.text.toString()

            val message = "*Pedido ${selected_item?.getTitulo()}:* \r\n\r\n" +
                    "Status -> ${selected_item?.getStatus()}"

            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }

        builder.show()
    }
    suspend fun getOrdensServico(ordensServicoList: MutableList<Order>) {
        // Nome da coleção
        val collectionName = "ordens-servico"

        // Obter a referência da coleção
        val collectionRef = FirebaseFirestore.getInstance().collection(collectionName)

        try {
            withContext(Dispatchers.IO) {
                // Obter os dados da coleção de forma assíncrona
                val querySnapshot = collectionRef.get().await()

                // Obter os dados
                for (document in querySnapshot.documents) {
                    val titulo = document.getString("titulo")
                    val descricao = document.getString("descricao")
                    val status = document.getString("status")
                    val ordemServico = Order(titulo, descricao, status)
                    ordensServicoList.add(ordemServico)
                }
            }
        } catch (exception: Exception) {
            // Tratar falha na obtenção dos dados
            println("Erro ao obter os dados: $exception")
        }
    }
}
