package xyz.scoca.helpark.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentProfileBinding
import xyz.scoca.helpark.model.user.UserInfo
import xyz.scoca.helpark.network.NetworkHelper
import xyz.scoca.helpark.util.extension.hideKeyboard
import xyz.scoca.helpark.util.extension.snack

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        fetchUserData()
        initListeners()

        binding.btnSave.setOnClickListener {
            hideKeyboard()
            validateProfileChanges()
        }
        binding.btnDeleteAccount.setOnClickListener {
            //Show Alert Dialog first.
            deleteAccount()
        }
        binding.tvUserEmail.text = ClientPreferences(requireContext()).getUserEmail()
        return binding.root
    }

    private fun fetchUserData() {
        var userName = ""
        val userEmail = ClientPreferences(requireContext()).getUserEmail().toString().trim()

        NetworkHelper().authService?.fetchUserData(userEmail)
            ?.enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    val result = response.body()?.error
                    if (result == false) {
                        userName = response.body()?.user?.name.toString()
                        binding.etProfileName.setText(userName)
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    snack(requireView(), "User name could not fetch.")
                }
            })
    }

    private fun initListeners() {
        binding.etProfilePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }

        })
    }

    private fun checkFields() {
        if (!binding.etProfilePassword.text.isNullOrEmpty()) {
            binding.btnDeleteAccount.isEnabled = true
            binding.btnSave.isEnabled = true
            binding.btnDeleteAccount.alpha = 1F
            binding.btnSave.alpha = 1F
        } else {
            binding.btnDeleteAccount.isEnabled = false
            binding.btnSave.isEnabled = false
            binding.btnDeleteAccount.alpha = 0.5F
            binding.btnSave.alpha = 0.5F
        }
    }

    private fun validateProfileChanges() {
        val name = binding.etProfileName.text.toString()
        val email = binding.tvUserEmail.text.toString()
        val password = binding.etProfilePassword.text.toString()

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            snack(requireView(), "Please fill out all places.")
        } else{
            updateProfile(name, email)
        }
    }

    private fun updateProfile(name: String, email: String) {
        NetworkHelper().authService?.updateUserData(name, email)
            ?.enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    snack(requireView(), "Successfully updated.")
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }

            })
    }

    private fun deleteAccount() {
        val email = ClientPreferences(requireContext()).getUserEmail().toString()
        NetworkHelper().authService?.deleteAccount(email)
            ?.enqueue(object : Callback<UserInfo> {
                override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                    snack(requireView(), "Account Deleted.")
                    ClientPreferences(requireContext()).clearSharedPref()
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    snack(requireView(), t.message.toString())
                }
            })
    }
}