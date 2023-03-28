package br.com.tasksmanager

import java.text.SimpleDateFormat
import java.util.Locale

class DateBrazilianFormact {

    companion object {
        fun formatDate(inputDate: String): String {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date!!)
        }
    }
}