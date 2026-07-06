package br.com.fluxocaixa.util

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar

object DatePickerHelper {

    fun mostrar(
        context: Context,
        dataMillis: Long,
        onDataSelecionada: (Long) -> Unit
    ) {
        val calendar = Calendar.getInstance().apply { timeInMillis = dataMillis }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDataSelecionada(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
