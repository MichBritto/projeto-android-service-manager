package model

import java.time.format.DateTimeFormatter
import java.util.*

class Order {
    private var titulo: String? = null
    private var descricao: String? = null
    private var status: String? = null
    private var comentario: Array<String>? = null
    private var data: String? = null

    constructor(titulo: String?, descricao: String?, status: String?) {
        this.titulo = titulo
        this.descricao = descricao
        this.status = status
        this.data = Calendar.getInstance().time.toString()
    }

    constructor(titulo: String?, descricao: String?, status: String?,data: String?) {
        this.titulo = titulo
        this.descricao = descricao
        this.status = status
        this.data = Calendar.getInstance().time.toString()
    }

    fun getTitulo(): String? {
        return titulo;
    }
    fun getDescricao(): String? {
        return descricao
    }

    fun getStatus(): String? {
        return status
    }

    fun getComentario(): Array<String>? {
        return comentario
    }

    fun getData(): String? {
        return data
    }


}