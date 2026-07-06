package br.com.fluxocaixa.ui.extrato

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento
import br.com.fluxocaixa.data.repository.LancamentoRepository

class ExtratoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LancamentoRepository.getInstance(application)
    private var listaCompleta: List<Lancamento> = emptyList()

    private val _lancamentos = MutableLiveData<List<Lancamento>>()
    val lancamentos: LiveData<List<Lancamento>> = _lancamentos

    private val _saldo = MutableLiveData<Double>()
    val saldo: LiveData<Double> = _saldo

    private val _filtro = MutableLiveData(FiltroTipo.TODOS)
    val filtro: LiveData<FiltroTipo> = _filtro

    fun carregar() {
        listaCompleta = repository.listar()
        _saldo.value = repository.calcularSaldo(listaCompleta)
        aplicarFiltro()
    }

    fun definirFiltro(filtro: FiltroTipo) {
        _filtro.value = filtro
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtroAtual = _filtro.value ?: FiltroTipo.TODOS
        _lancamentos.value = when (filtroAtual) {
            FiltroTipo.RECEITA -> listaCompleta.filter { it.tipo == TipoLancamento.RECEITA }
            FiltroTipo.DESPESA -> listaCompleta.filter { it.tipo == TipoLancamento.DESPESA }
            FiltroTipo.TODOS -> listaCompleta
        }
    }
}
