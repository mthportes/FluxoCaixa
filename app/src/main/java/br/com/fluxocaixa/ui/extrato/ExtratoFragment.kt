package br.com.fluxocaixa.ui.extrato

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.fluxocaixa.R
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento
import br.com.fluxocaixa.databinding.FragmentExtratoBinding
import br.com.fluxocaixa.ui.lancamento.LancamentoViewModel
import br.com.fluxocaixa.util.DatePickerHelper
import br.com.fluxocaixa.util.FeedbackSnack
import br.com.fluxocaixa.util.Formatters
import br.com.fluxocaixa.util.MoedaTextWatcher

class ExtratoFragment : Fragment() {

    private var _binding: FragmentExtratoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExtratoViewModel by viewModels()
    private val lancamentoViewModel: LancamentoViewModel by viewModels()
    private val adapter = LancamentoAdapter()
    private var assistenteExpandido = false
    private var tipoFormularioAssistente: TipoLancamento? = null
    private var formularioAtivo = false
    private var dataSelecionadaAssistente = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtratoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerLancamentos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLancamentos.adapter = adapter
        configurarAssistente()
        configurarFiltro()
        observarViewModel()
        observarLancamentoViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregar()
    }

    private fun configurarAssistente() {
        atualizarAssistente(expandido = false, animar = false)
        binding.assistente.inputValorAssistente.addTextChangedListener(
            MoedaTextWatcher(binding.assistente.inputValorAssistente)
        )
        binding.assistente.inputDataAssistente.setOnClickListener { abrirDatePickerAssistente() }
        binding.assistente.layoutDataAssistente.setEndIconOnClickListener { abrirDatePickerAssistente() }

        binding.assistente.headerAssistente.setOnClickListener {
            when {
                formularioAtivo -> fecharFormulario()
                else -> atualizarAssistente(expandido = !assistenteExpandido, animar = true)
            }
        }
        binding.assistente.buttonAtalhoReceita.setOnClickListener {
            selecionarAtalho(TipoLancamento.RECEITA)
        }
        binding.assistente.buttonAtalhoDespesa.setOnClickListener {
            selecionarAtalho(TipoLancamento.DESPESA)
        }
        binding.assistente.buttonSalvarAssistente.setOnClickListener {
            salvarLancamentoAssistente()
        }
        binding.assistente.buttonFecharAssistente.setOnClickListener {
            fecharFormulario()
        }
    }

    private fun selecionarAtalho(tipo: TipoLancamento) {
        tipoFormularioAssistente = tipo
        formularioAtivo = true
        dataSelecionadaAssistente = System.currentTimeMillis()
        binding.assistente.inputValorAssistente.text?.clear()
        binding.assistente.inputDescricaoAssistente.text?.clear()
        atualizarCampoDataAssistente()

        val receita = tipo == TipoLancamento.RECEITA
        binding.assistente.chipTipoFormulario.setBackgroundResource(
            if (receita) R.drawable.bg_chip_receita else R.drawable.bg_chip_despesa
        )
        binding.assistente.chipTipoFormulario.text = getString(
            if (receita) R.string.label_tipo_receita else R.string.label_tipo_despesa
        )
        binding.assistente.textAssistenteSubtitulo.text = getString(
            if (receita) R.string.atalho_receita else R.string.atalho_despesa
        )

        if (!assistenteExpandido) {
            atualizarAssistente(expandido = true, animar = true)
        } else {
            atualizarVisibilidadeAssistente()
        }
    }

    private fun fecharFormulario() {
        formularioAtivo = false
        tipoFormularioAssistente = null
        binding.assistente.inputValorAssistente.text?.clear()
        binding.assistente.inputDescricaoAssistente.text?.clear()
        atualizarVisibilidadeAssistente()
        binding.assistente.textAssistenteSubtitulo.text = getString(R.string.assistente_subtitulo)
    }

    private fun atualizarVisibilidadeAssistente() {
        binding.assistente.layoutAtalhos.isVisible = assistenteExpandido && !formularioAtivo
        binding.assistente.layoutFormulario.isVisible = assistenteExpandido && formularioAtivo
    }

    private fun abrirDatePickerAssistente() {
        DatePickerHelper.mostrar(requireContext(), dataSelecionadaAssistente) {
            dataSelecionadaAssistente = it
            atualizarCampoDataAssistente()
        }
    }

    private fun atualizarCampoDataAssistente() {
        binding.assistente.inputDataAssistente.setText(Formatters.data(dataSelecionadaAssistente))
    }

    private fun salvarLancamentoAssistente() {
        val tipo = tipoFormularioAssistente ?: return
        lancamentoViewModel.salvar(
            valorTexto = binding.assistente.inputValorAssistente.text?.toString().orEmpty(),
            descricao = binding.assistente.inputDescricaoAssistente.text?.toString().orEmpty(),
            dataMillis = dataSelecionadaAssistente,
            tipo = tipo
        )
    }

    private fun atualizarAssistente(expandido: Boolean, animar: Boolean) {
        assistenteExpandido = expandido
        val atalhos = binding.assistente.layoutAtalhos
        val formulario = binding.assistente.layoutFormulario
        val icone = binding.assistente.iconExpandir

        if (!expandido) {
            tipoFormularioAssistente = null
            formularioAtivo = false
            formulario.isVisible = false
        }

        if (animar) {
            if (expandido) {
                if (formularioAtivo) {
                    formulario.alpha = 0f
                    formulario.isVisible = true
                    atalhos.isVisible = false
                    formulario.animate().alpha(1f).setDuration(240).start()
                } else {
                    atalhos.alpha = 0f
                    atalhos.isVisible = true
                    formulario.isVisible = false
                    atalhos.animate().alpha(1f).setDuration(240).start()
                }
            } else {
                atalhos.animate().alpha(0f).setDuration(200).withEndAction {
                    atalhos.isVisible = false
                    atalhos.alpha = 1f
                }.start()
                formulario.animate().alpha(0f).setDuration(200).withEndAction {
                    formulario.isVisible = false
                    formulario.alpha = 1f
                }.start()
            }
            icone.animate()
                .rotation(if (expandido) 180f else 0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(240)
                .start()
        } else {
            atualizarVisibilidadeAssistente()
            icone.rotation = if (expandido) 180f else 0f
        }

        if (tipoFormularioAssistente == null && !formularioAtivo) {
            binding.assistente.textAssistenteSubtitulo.text = getString(R.string.assistente_subtitulo)
        }
    }

    private fun configurarFiltro() {
        binding.buttonFiltroTodos.setOnClickListener {
            viewModel.definirFiltro(FiltroTipo.TODOS)
        }
        binding.buttonFiltroReceita.setOnClickListener {
            viewModel.definirFiltro(FiltroTipo.RECEITA)
        }
        binding.buttonFiltroDespesa.setOnClickListener {
            viewModel.definirFiltro(FiltroTipo.DESPESA)
        }
    }

    private fun atualizarFiltroUi(filtro: FiltroTipo) {
        val context = requireContext()

        binding.buttonFiltroTodos.setBackgroundResource(
            if (filtro == FiltroTipo.TODOS) R.drawable.bg_filter_todos_selected else R.drawable.bg_filter_chip
        )
        binding.buttonFiltroTodos.setTextColor(
            ContextCompat.getColor(context, if (filtro == FiltroTipo.TODOS) R.color.primary_dark else R.color.on_surface_variant)
        )

        binding.buttonFiltroReceita.setBackgroundResource(
            if (filtro == FiltroTipo.RECEITA) R.drawable.bg_filter_receita_selected else R.drawable.bg_filter_chip
        )
        binding.buttonFiltroReceita.setTextColor(
            ContextCompat.getColor(context, if (filtro == FiltroTipo.RECEITA) R.color.receita_dark else R.color.on_surface_variant)
        )

        binding.buttonFiltroDespesa.setBackgroundResource(
            if (filtro == FiltroTipo.DESPESA) R.drawable.bg_filter_despesa_selected else R.drawable.bg_filter_chip
        )
        binding.buttonFiltroDespesa.setTextColor(
            ContextCompat.getColor(context, if (filtro == FiltroTipo.DESPESA) R.color.despesa_dark else R.color.on_surface_variant)
        )
    }

    private fun atualizarEstadoVazio(lista: List<Lancamento>, filtro: FiltroTipo) {
        val vazio = lista.isEmpty()
        binding.layoutVazio.visibility = if (vazio) View.VISIBLE else View.GONE
        binding.recyclerLancamentos.visibility = if (vazio) View.GONE else View.VISIBLE

        if (!vazio) return

        when (filtro) {
            FiltroTipo.RECEITA -> {
                binding.textListaVazia.setText(R.string.lista_vazia_filtro_receita)
                binding.textListaVaziaDica.setText(R.string.lista_vazia_filtro_dica)
            }
            FiltroTipo.DESPESA -> {
                binding.textListaVazia.setText(R.string.lista_vazia_filtro_despesa)
                binding.textListaVaziaDica.setText(R.string.lista_vazia_filtro_dica)
            }
            FiltroTipo.TODOS -> {
                binding.textListaVazia.setText(R.string.lista_vazia)
                binding.textListaVaziaDica.setText(R.string.lista_vazia_dica)
            }
        }
    }

    private fun observarViewModel() {
        viewModel.lancamentos.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            val filtro = viewModel.filtro.value ?: FiltroTipo.TODOS
            atualizarEstadoVazio(lista, filtro)
        }

        viewModel.filtro.observe(viewLifecycleOwner) { filtro ->
            atualizarFiltroUi(filtro)
            val lista = viewModel.lancamentos.value.orEmpty()
            atualizarEstadoVazio(lista, filtro)
        }

        viewModel.saldo.observe(viewLifecycleOwner) { saldo ->
            binding.textSaldo.text = Formatters.moeda(saldo)
        }
    }

    private fun observarLancamentoViewModel() {
        lancamentoViewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                FeedbackSnack.mostrar(binding.root, it.texto, it.sucesso)
                lancamentoViewModel.limparFeedback()
            }
        }

        lancamentoViewModel.salvoComSucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                binding.assistente.inputValorAssistente.text?.clear()
                binding.assistente.inputDescricaoAssistente.text?.clear()
                dataSelecionadaAssistente = System.currentTimeMillis()
                atualizarCampoDataAssistente()
                viewModel.carregar()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
