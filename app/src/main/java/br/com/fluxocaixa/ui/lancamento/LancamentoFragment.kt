package br.com.fluxocaixa.ui.lancamento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.fluxocaixa.data.model.TipoLancamento
import br.com.fluxocaixa.databinding.FragmentLancamentoBinding
import br.com.fluxocaixa.util.DatePickerHelper
import br.com.fluxocaixa.util.FeedbackSnack
import br.com.fluxocaixa.util.Formatters
import br.com.fluxocaixa.util.MoedaTextWatcher

class LancamentoFragment : Fragment() {

    private var _binding: FragmentLancamentoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LancamentoViewModel by viewModels()
    private var dataSelecionada = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLancamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarTipo()
        configurarValor()
        configurarData()
        configurarAcoes()
        observarViewModel()
    }

    private fun configurarTipo() {
        val opcoes = TipoLancamento.entries.map { it.label }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcoes)
        binding.spinnerTipo.adapter = adapter
    }

    private fun configurarValor() {
        binding.inputValor.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        binding.inputValor.addTextChangedListener(MoedaTextWatcher(binding.inputValor))
    }

    private fun configurarData() {
        atualizarCampoData()
        binding.inputData.setOnClickListener { abrirDatePicker() }
        binding.layoutData.setEndIconOnClickListener { abrirDatePicker() }
    }

    private fun abrirDatePicker() {
        DatePickerHelper.mostrar(requireContext(), dataSelecionada) { millis ->
            dataSelecionada = millis
            atualizarCampoData()
        }
    }

    private fun atualizarCampoData() {
        binding.inputData.setText(Formatters.data(dataSelecionada))
    }

    private fun configurarAcoes() {
        binding.buttonSalvar.setOnClickListener { salvarLancamento() }
    }

    private fun salvarLancamento() {
        viewModel.salvar(
            valorTexto = binding.inputValor.text?.toString().orEmpty(),
            descricao = binding.inputDescricao.text?.toString().orEmpty(),
            dataMillis = dataSelecionada,
            tipo = TipoLancamento.fromSpinnerIndex(binding.spinnerTipo.selectedItemPosition)
        )
    }

    private fun observarViewModel() {
        viewModel.feedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let {
                FeedbackSnack.mostrar(binding.root, it.texto, it.sucesso)
                viewModel.limparFeedback()
            }
        }

        viewModel.salvoComSucesso.observe(viewLifecycleOwner) { sucesso ->
            if (sucesso) {
                binding.inputValor.text?.clear()
                binding.inputDescricao.text?.clear()
                dataSelecionada = System.currentTimeMillis()
                atualizarCampoData()
                binding.spinnerTipo.setSelection(TipoLancamento.RECEITA.ordinal)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
