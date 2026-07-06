package br.com.fluxocaixa.ui.lancamento

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.fluxocaixa.R
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento
import br.com.fluxocaixa.data.repository.LancamentoRepository
import br.com.fluxocaixa.util.Formatters

class LancamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LancamentoRepository.getInstance(application)
    private val app = getApplication<Application>()

    private val _feedback = MutableLiveData<Feedback?>()
    val feedback: LiveData<Feedback?> = _feedback

    private val _salvoComSucesso = MutableLiveData(false)
    val salvoComSucesso: LiveData<Boolean> = _salvoComSucesso

    fun salvar(valorTexto: String, descricao: String, dataMillis: Long, tipo: TipoLancamento) {
        val valor = Formatters.parseValor(valorTexto)
        when {
            valor == null || valor <= 0 -> {
                _feedback.value = Feedback(app.getString(R.string.feedback_valor_invalido), false)
                _salvoComSucesso.value = false
            }
            descricao.isBlank() -> {
                _feedback.value = Feedback(app.getString(R.string.feedback_descricao_vazia), false)
                _salvoComSucesso.value = false
            }
            else -> {
                val lancamento = Lancamento(
                    valor = valor,
                    descricao = descricao.trim(),
                    dataMillis = Formatters.inicioDoDia(dataMillis),
                    tipo = tipo
                )
                repository.salvar(lancamento)
                _feedback.value = Feedback(app.getString(R.string.feedback_salvo_sucesso), true)
                _salvoComSucesso.value = true
            }
        }
    }

    fun limparFeedback() {
        _feedback.value = null
        _salvoComSucesso.value = false
    }
}
