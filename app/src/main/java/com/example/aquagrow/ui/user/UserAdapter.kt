package com.example.aquagrow.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import com.example.aquagrow.data.model.responses.UserComReponse

class UserAdapter(
    private var users: List<UserComReponse>,
    private val onEditClick: (UserComReponse) -> Unit,
    private val onDeleteClick: (UserComReponse) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

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
        val user = users[position]

        holder.tvName.text = user.userInfo?.usuario ?: "N/A"
        holder.tvType.text = user.tipo_usuario?.nombre ?: "N/A"
        holder.tvStatus.text = if (user.userInfo?.estatus == 1) "Activo" else "Inactivo"

        holder.btnEdit.setOnClickListener { onEditClick(user) }
        holder.btnDelete.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateList(newUsers: List<UserComReponse>) {
        users = newUsers
        notifyDataSetChanged()
    }
}