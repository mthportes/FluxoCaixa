package br.com.fluxocaixa.data.repository

import android.content.Context
import br.com.fluxocaixa.data.database.FluxoCaixaDatabaseHelper
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento

class LancamentoRepository private constructor(context: Context) {

    private val database = FluxoCaixaDatabaseHelper.getInstance(context)

    fun salvar(lancamento: Lancamento): Long = database.inserir(lancamento)

    fun listar(): List<Lancamento> = database.listarTodos()

    fun calcularSaldo(lancamentos: List<Lancamento>): Double {
        return lancamentos.sumOf { lancamento ->
            when (lancamento.tipo) {
                TipoLancamento.RECEITA -> lancamento.valor
                TipoLancamento.DESPESA -> -lancamento.valor
            }
        }
    }

    companion object {
        @Volatile
        private var instance: LancamentoRepository? = null

        fun getInstance(context: Context): LancamentoRepository {
            return instance ?: synchronized(this) {
                instance ?: LancamentoRepository(context).also { instance = it }
            }
        }
    }
}
