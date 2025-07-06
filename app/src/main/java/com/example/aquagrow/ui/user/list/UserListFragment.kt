package com.example.aquagrow.ui.user.list

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.ui.user.UserAdapter
import com.example.aquagrow.ui.user.form.UserFormFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    private lateinit var adapter: UserAdapter
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var etSearch: TextInputEditText

    private val viewModel: UserListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)

        // Inicializar vistas usando findViewById
        rvUsers = view.findViewById(R.id.rvUsers)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        fabAddUser = view.findViewById(R.id.fabAddUser)
        etSearch = view.findViewById(R.id.etSearch)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvUsers.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.layout_animation_fall_down
        )

        setupObservers()
        setupRecyclerView()
        setupListeners()
        setupRefreshObserver()
        setupResultListener()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            emptyList(),
            onEditClick = { user -> navigateToEditUser(user) },
            onDeleteClick = { user ->
                showDeleteConfirmationDialog(user)
            }
        )

        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect{state ->
                    when (state) {
                        UserListState.Loading -> showLoading(true)
                        is UserListState.Success -> {
                            showLoading(false)
                            if (state.users.isNotEmpty()) {
                                adapter.updateList(state.users)
                                rvUsers.visibility = View.VISIBLE
                                tvEmptyState.visibility = View.GONE
                            } else {
                                rvUsers.visibility = View.GONE
                                tvEmptyState.visibility = View.VISIBLE
                            }
                        }
                        is UserListState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                        UserListState.Empty -> {
                            showLoading(false)
                            rvUsers.visibility = View.GONE
                            tvEmptyState.visibility = View.VISIBLE
                        }
                        is UserListState.DeleteSuccess -> {
                            showLoading(false)
                            showSuccess("Usuario eliminado correctamente")
                            viewModel.loadUsers()
                        }
                    }
                }
            }
        }
    }

    private fun setupRefreshObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refreshEvent.collect {
                    loadUsers()
                }
            }
        }
    }

    private fun loadUsers() {
        viewModel.loadUsers()
    }

    private fun setupListeners() {
        fabAddUser.setOnClickListener {
            navigateToCreateUser()
        }
    }

    // Para crear nuevo usuario
    private fun navigateToCreateUser() {
        // Usar 0 para indicar nuevo usuario
        val fragment = UserFormFragment.newInstance(0)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("user_form")
            .commit()
    }

    // Para editar usuario
    private fun navigateToEditUser(user: UserComReponse) {
        val userId = user.userInfo?.id_usuario ?: 0
        val fragment = UserFormFragment.newInstance(userId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("user_form")
            .commit()
    }

    private fun setupResultListener() {
        parentFragmentManager.setFragmentResultListener("REFRESH_REQUEST", viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean("SHOULD_REFRESH", false)) {
                // Recargar con animación
                rvUsers.scheduleLayoutAnimation()
                viewModel.loadUsers()
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    private fun showDeleteConfirmationDialog(user: UserComReponse) {
        val userId = user.userInfo?.id_usuario ?: 0
        val userName = user.userInfo?.primer_nombre ?: "este usuario"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar a $userName?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                viewModel.deleteUser(userId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.baseline_warning_24)
            .show()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvUsers.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showSuccess(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.teal_700))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .setAction("Reintentar") { viewModel.loadUsers() }
                .show()
        }
    }
}