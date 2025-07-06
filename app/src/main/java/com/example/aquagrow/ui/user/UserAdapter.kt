package com.example.aquagrow.ui.user

import android.widget.Filter
import android.widget.Filterable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import com.example.aquagrow.data.model.responses.UserComReponse

class UserAdapter(
    private var users: List<UserComReponse>,
    private val onEditClick: (UserComReponse) -> Unit,
    private val onDeleteClick: (UserComReponse) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), Filterable {

    private var originalList: List<UserComReponse> = users
    private var filteredList: List<UserComReponse> = users

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvType: TextView = itemView.findViewById(R.id.tvUserType)
        val tvStatus: TextView = itemView.findViewById(R.id.tvUserStatus)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditUser)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredList[position]

        val nombre = user.userInfo?.primer_nombre ?: ""
        val app = user.userInfo?.apellido_pat ?: ""
        val usuario = user.userInfo?.usuario ?: ""
        val texto = "$nombre $app ($usuario)"
        holder.tvName.text = texto
        holder.tvType.text = user.tipo_usuario?.nombre ?: "N/A"
        holder.tvStatus.text = if (user.userInfo?.estatus == 1) "Activo" else "Inactivo"

        holder.btnEdit.setOnClickListener { onEditClick(user) }
        holder.btnDelete.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtered = if (constraint.isNullOrEmpty()) {
                    originalList
                } else {
                    val query = constraint.toString().lowercase().trim()
                    originalList.filter { user ->
                        user.userInfo?.primer_nombre?.lowercase()?.contains(query) == true ||
                                user.userInfo?.apellido_pat?.lowercase()?.contains(query) == true ||
                                user.userInfo?.usuario?.lowercase()?.contains(query) == true ||
                                user.tipo_usuario?.nombre?.lowercase()?.contains(query) == true
                    }
                }

                return FilterResults().apply {
                    values = filtered
                    count = filtered.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<UserComReponse> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newUsers: List<UserComReponse>) {
        val oldList = originalList
        originalList = newUsers
        filteredList = newUsers

        if (newUsers.isEmpty()) {
            notifyDataSetChanged()
        } else {
            // Usar DiffUtil para animaciones y actualizaciones eficientes
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldList.size
                override fun getNewListSize(): Int = newUsers.size

                override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                    return oldList[oldPos].userInfo?.id_usuario == newUsers[newPos].userInfo?.id_usuario
                }

                override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                    return oldList[oldPos] == newUsers[newPos]
                }
            })

            diffResult.dispatchUpdatesTo(this)
        }
    }
}