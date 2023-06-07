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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import model.Order

class MenuActivity : AppCompatActivity(), OrderAdapter.OnOrderClickListener {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var adapter: OrderAdapter
    private var selected_item: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val orders = listOf(
            Order("heo3k3ms", "Order 1", "Description", "Accepted"),
            Order("heo3k3ds", "Order 2", "Description","Accepted"),
            Order("hfo3k3ms", "Order 3", "Description","Accepted")
        )
        val email = intent.getStringExtra("email")
        val nome = intent.getStringExtra("nome")
        print("email: " + email  + "Nome:" + nome)



        adapter = OrderAdapter(this, orders, this)
        binding.ordemServico.layoutManager = LinearLayoutManager(this)
        binding.ordemServico.adapter = adapter

        binding.addOrderButton.setOnClickListener {
            Toast.makeText(this, "Clicou no botão", Toast.LENGTH_LONG).show()
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }

        binding.editarPerfilButton.setOnClickListener{
            val intent = Intent(this, ProfileChangeActivity::class.java)
            startActivity(intent)
        }

        binding.btnDetailOrder.setOnClickListener {
            if (selected_item != null) {
                val intent = Intent(this, OrderDetailActivity::class.java)
                startActivity(intent)
            }
        }
        binding.editarPerfilButton.setOnClickListener {
                val intent = Intent(this, ProfileChangeActivity::class.java)
                startActivity(intent)
        }

    }

    override fun onOrderClick(order: Order) {
        selected_item = order
    }
    // Get a reference to the node you want to retrieve data from
    val ordersRef = FirebaseDatabase.getInstance().getReference("ordens-servico")

    // Attach a listener to retrieve the data
    val ordersListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Iterate through the children of the node
            for (orderSnapshot in dataSnapshot.children) {
                // Get the values of the order
                val order = orderSnapshot.child("order").getValue(String::class.java)
                val descricao = orderSnapshot.child("descricao").getValue(String::class.java)
                val status = orderSnapshot.child("status").getValue(String::class.java)
                val comentario = orderSnapshot.child("comentario").getValue(Array<String>::class.java)
                val data = orderSnapshot.child("data").getValue(String::class.java)

                // Do something with the order data
                Log.d("Order", "Order: $order, Descrição: $descricao, Status: $status, Comentário: ${comentario?.joinToString()}, Data: $data")
            }
        }


        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors
            Log.e("Error", "Error retrieving orders: $databaseError")
        }
    }


}
