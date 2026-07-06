package br.com.fluxocaixa.data.model

enum class TipoLancamento(val label: String, val codigo: String) {
    RECEITA("Receita", "C"),
    DESPESA("Despesa", "D");

    companion object {
        fun fromSpinnerIndex(index: Int): TipoLancamento {
            return if (index == DESPESA.ordinal) DESPESA else RECEITA
        }
    }
}

data class Lancamento(
    val id: Long = 0,
    val valor: Double,
    val descricao: String,
    val dataMillis: Long,
    val tipo: TipoLancamento
)
