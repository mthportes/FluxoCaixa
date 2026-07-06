package br.com.fluxocaixa.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class MoedaTextWatcher(
    private val campo: TextInputEditText
) : TextWatcher {

    private var atualizando = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(editable: Editable?) {
        if (atualizando) return

        val digitos = editable?.toString()?.filter { it.isDigit() }.orEmpty()
        val formatado = if (digitos.isEmpty()) {
            ""
        } else {
            Formatters.formatarEntradaMoeda(digitos)
        }

        if (formatado == editable?.toString()) return

        atualizando = true
        campo.setText(formatado)
        campo.setSelection(formatado.length)
        atualizando = false
    }
}
