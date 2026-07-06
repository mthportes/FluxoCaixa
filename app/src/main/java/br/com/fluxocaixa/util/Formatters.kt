package br.com.fluxocaixa.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {

    private val locale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(locale)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", locale)

    fun moeda(valor: Double): String = currencyFormat.format(valor)

    fun data(millis: Long): String = dateFormat.format(Date(millis))

    fun inicioDoDia(millis: Long): Long {
        val calendar = Calendar.getInstance(locale).apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun formatarEntradaMoeda(digitos: String): String {
        val centavos = digitos.toLongOrNull() ?: return ""
        val valor = centavos / 100.0
        val numero = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        return numero.format(valor)
    }

    fun parseValor(texto: String): Double? {
        val limpo = texto.trim()
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")
            .replace(".", "")
            .replace(",", ".")
        if (limpo.isBlank()) return null
        return limpo.toDoubleOrNull()
    }
}
