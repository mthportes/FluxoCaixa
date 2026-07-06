package br.com.fluxocaixa.ui.extrato

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fluxocaixa.R
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento
import br.com.fluxocaixa.databinding.ItemLancamentoBinding
import br.com.fluxocaixa.util.Formatters

class LancamentoAdapter :
    ListAdapter<Lancamento, LancamentoAdapter.LancamentoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LancamentoViewHolder {
        val binding = ItemLancamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LancamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LancamentoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LancamentoViewHolder(
        private val binding: ItemLancamentoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lancamento: Lancamento) {
            val context = binding.root.context
            val receita = lancamento.tipo == TipoLancamento.RECEITA

            binding.textTipo.text = lancamento.tipo.codigo
            binding.textDescricao.text = lancamento.descricao
            binding.textData.text = Formatters.data(lancamento.dataMillis)
            binding.textValor.text = Formatters.moeda(lancamento.valor)

            val corPrincipal = ContextCompat.getColor(
                context,
                if (receita) R.color.receita_dark else R.color.despesa_dark
            )

            binding.layoutItem.setBackgroundResource(
                if (receita) R.drawable.bg_item_receita else R.drawable.bg_item_despesa
            )
            binding.textTipo.setTextColor(corPrincipal)
            binding.textValor.setTextColor(corPrincipal)
            binding.indicatorTipo.setBackgroundColor(corPrincipal)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Lancamento>() {
        override fun areItemsTheSame(oldItem: Lancamento, newItem: Lancamento): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lancamento, newItem: Lancamento): Boolean {
            return oldItem == newItem
        }
    }
}
