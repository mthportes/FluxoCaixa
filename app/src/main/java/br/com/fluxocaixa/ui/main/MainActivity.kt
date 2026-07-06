package br.com.fluxocaixa.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import br.com.fluxocaixa.R
import br.com.fluxocaixa.databinding.ActivityMainBinding
import br.com.fluxocaixa.ui.extrato.ExtratoFragment
import br.com.fluxocaixa.ui.lancamento.LancamentoFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSafeArea()
        binding.buttonMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            abrirFragment(ExtratoFragment(), R.string.titulo_extrato)
            binding.navigationView.setCheckedItem(R.id.nav_extrato)
        }
    }

    private fun configurarSafeArea() {
        val paddingVertical = resources.getDimensionPixelSize(R.dimen.toolbar_padding_vertical)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarContainer) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top + paddingVertical,
                bottom = paddingVertical
            )
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainer) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            windowInsets
        }

        val header = binding.navigationView.getHeaderView(0)
        ViewCompat.setOnApplyWindowInsetsListener(header) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top + 20)
            windowInsets
        }
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_lancamento -> abrirFragment(LancamentoFragment(), R.string.titulo_lancamento)
            R.id.nav_extrato -> abrirFragment(ExtratoFragment(), R.string.titulo_extrato)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun abrirFragment(fragment: Fragment, tituloRes: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        binding.textToolbarTitle.text = getString(tituloRes)
        binding.textToolbarSubtitle.text = when (tituloRes) {
            R.string.titulo_extrato -> getString(R.string.label_saldo)
            else -> getString(R.string.nav_subtitulo)
        }
    }
}
