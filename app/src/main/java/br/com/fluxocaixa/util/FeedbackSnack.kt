package br.com.fluxocaixa.util

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.updatePadding
import br.com.fluxocaixa.R
import br.com.fluxocaixa.databinding.LayoutSnackbarFeedbackBinding
import com.google.android.material.snackbar.Snackbar

object FeedbackSnack {

    fun mostrar(ancora: View, mensagem: String, sucesso: Boolean) {
        val snackbar = Snackbar.make(ancora, "", Snackbar.LENGTH_LONG)
        val context = ancora.context
        val binding = LayoutSnackbarFeedbackBinding.inflate(LayoutInflater.from(context))

        binding.layoutSnackbar.setBackgroundResource(
            if (sucesso) R.drawable.bg_snackbar_success else R.drawable.bg_snackbar_error
        )
        binding.iconSnackbar.setImageResource(
            if (sucesso) R.drawable.ic_feedback_success else R.drawable.ic_feedback_error
        )
        binding.textSnackbar.text = mensagem

        val layout = snackbar.view as Snackbar.SnackbarLayout
        layout.setBackgroundColor(context.getColor(android.R.color.transparent))
        layout.updatePadding(left = 24, top = 0, right = 24, bottom = 24)
        layout.elevation = 8f

        val textoPadrao = layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textoPadrao.visibility = View.INVISIBLE
        textoPadrao.minimumHeight = 0

        val acaoPadrao = layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        acaoPadrao.visibility = View.GONE

        layout.addView(binding.root, 0, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackbar.show()
    }
}
