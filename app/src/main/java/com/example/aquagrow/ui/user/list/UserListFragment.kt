package com.example.aquagrow.ui.user.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.ui.user.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    private lateinit var viewModel: UserListViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var fabAddUser: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)

        // Inicializar vistas usando findViewById
        rvUsers = view.findViewById(R.id.rvUsers)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        fabAddUser = view.findViewById(R.id.fabAddUser)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(UserListViewModel::class.java)
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            emptyList(),
            onEditClick = { user -> navigateToEditUser(user) },
            onDeleteClick = { user ->
                viewModel.deleteUser(user.userInfo!!.id_usuario!!)
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
                            adapter.updateList(state.users)
                            tvEmptyState.visibility = View.GONE
                        }
                        is UserListState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                        UserListState.Empty -> {
                            showLoading(false)
                            tvEmptyState.visibility = View.VISIBLE
                        }
                        is UserListState.DeleteSuccess -> {
                            // Actualizar lista después de eliminar
                            viewModel.loadUsers()
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        fabAddUser.setOnClickListener {
            navigateToCreateUser()
        }
    }

    private fun navigateToCreateUser() {
//        val fragment = UserFormFragment.newInstance(null)
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
    }

    private fun navigateToEditUser(user: UserComReponse) {
//        val fragment = UserFormFragment.newInstance(user)
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        // Implementar Toast o Snackbar
    }
}