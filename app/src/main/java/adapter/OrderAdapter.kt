package adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.tasksmanager.R
import model.Order

import android.view.View
import android.widget.TextView
import data.OrderSingleton


class OrderAdapter(
    private val context: Context,
    private val orders: List<Order>,
    private val onOrderClickListener: OnOrderClickListener
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private var selectedItemPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false)
        return ViewHolder(itemView, onOrderClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position], position == selectedItemPosition)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class ViewHolder(itemView: View, onOrderClickListener: OnOrderClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val nomeTextView: TextView = itemView.findViewById(R.id.list_row_title)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_pedido)

        private lateinit var onOrderClickListener: OnOrderClickListener
        private lateinit var order: Order

        init {
            itemView.setOnClickListener(this)
            this.onOrderClickListener = onOrderClickListener
        }

        fun bind(order: Order, isSelected: Boolean) {
            this.order = order
            nomeTextView.text = order.getTitulo()
            statusTextView.text = order.getStatus()

            itemView.setBackgroundColor(if (isSelected) Color.GRAY else Color.WHITE)
        }

        override fun onClick(view: View) {
            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = adapterPosition
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedItemPosition)
            onOrderClickListener.onOrderClick(order)
            OrderSingleton.setSelectedOrder(order)
        }
    }

    interface OnOrderClickListener {
        fun onOrderClick(order: Order)
    }
}
