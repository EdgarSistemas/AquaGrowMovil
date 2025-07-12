package com.example.aquagrow.ui.assignUnit

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import com.example.aquagrow.data.model.responses.UserInfoResponse
import kotlinx.coroutines.launch

class UserSearchDialogFragment(
    private val onUserSelected: (UserInfoResponse) -> Unit
) : DialogFragment() {

    private val viewModel: UserSearchViewModel by viewModels()

    private lateinit var etSearch: EditText
    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ArrayAdapter<String>

    private var userList: List<UserInfoResponse> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_user_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etSearch = view.findViewById(R.id.etSearchUser)
        listView = view.findViewById(R.id.lvUsers)
        progressBar = view.findViewById(R.id.progressBar)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        observeViewModel()

        viewModel.loadAllUsers()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.filterUsers(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = userList[position]
            onUserSelected(selectedUser)
            dismiss()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.users.collect { users ->
                        userList = users
                        adapter.clear()
                        adapter.addAll(users.map {
                            "${it.primer_nombre} ${it.apellido_pat} (${it.usuario})"
                        })
                        adapter.notifyDataSetChanged()
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }
}
