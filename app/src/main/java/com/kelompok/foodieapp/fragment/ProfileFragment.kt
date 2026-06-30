package com.kelompok.foodieapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok.foodieapp.LoginActivity
import com.kelompok.foodieapp.adapter.OrderHistoryAdapter
import com.kelompok.foodieapp.database.DatabaseHelper
import com.kelompok.foodieapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DatabaseHelper
    private lateinit var historyAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        // Setup RecyclerView riwayat
        historyAdapter = OrderHistoryAdapter(emptyList())
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrderHistory.adapter = historyAdapter

        loadProfile()

        // Ikon pensil — toggle form edit
        binding.btnEditName.setOnClickListener {
            val isVisible = binding.layoutEditName.visibility == View.VISIBLE
            binding.layoutEditName.visibility = if (isVisible) View.GONE else View.VISIBLE
            if (!isVisible) {
                binding.etEditName.setText(binding.tvProfileName.text)
                binding.etEditName.requestFocus()
            }
        }

        // Simpan perubahan nama
        binding.btnSaveName.setOnClickListener {
            val newName = binding.etEditName.text.toString().trim()
            val email   = requireActivity()
                .getSharedPreferences("session", 0)
                .getString("user_email", "") ?: ""

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val success = db.updateUser(email, newName)
            if (success) {
                requireActivity().getSharedPreferences("session", 0)
                    .edit()
                    .putString("user_name", newName)
                    .apply()
                binding.layoutEditName.visibility = View.GONE
                Toast.makeText(requireContext(), "Nama berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                loadProfile()
            } else {
                Toast.makeText(requireContext(), "Gagal memperbarui nama", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            requireActivity().getSharedPreferences("session", 0)
                .edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadProfile() {
        val prefs      = requireActivity().getSharedPreferences("session", 0)
        val name       = prefs.getString("user_name", "-") ?: "-"
        val email      = prefs.getString("user_email", "-") ?: "-"
        val totalItems = db.getTotalItemsOrdered(email)
        val history    = db.getOrderHistory()

        binding.tvProfileName.text  = name
        binding.tvProfileEmail.text = email
        binding.tvTotalItems.text   = "$totalItems item dipesan"
        binding.tvAvatar.text       = if (name.isNotEmpty())
            name[0].uppercaseChar().toString() else "?"

        if (history.isEmpty()) {
            binding.tvEmptyHistory.visibility  = View.VISIBLE
            binding.rvOrderHistory.visibility  = View.GONE
        } else {
            binding.tvEmptyHistory.visibility  = View.GONE
            binding.rvOrderHistory.visibility  = View.VISIBLE
            historyAdapter.updateData(history)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}