package xyz.scoca.helpark.ui.auth.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentRegisterBinding
import xyz.scoca.helpark.model.status.OnError
import xyz.scoca.helpark.model.status.OnSuccess
import xyz.scoca.helpark.util.extension.snack

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    private var name = ""
    private var email = ""
    private var password = ""
    private var repassword = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = RegisterViewModel()

        initObservers()

        binding.btnRegister.setOnClickListener {
            validateUser()
        }
        return binding.root
    }

    private fun initObservers() {
        viewModel.userDetail.observe(viewLifecycleOwner) { status ->
            when (status) {
                is OnSuccess -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    ClientPreferences(requireContext()).setUserEmail(email)
                    ClientPreferences(requireContext()).setRememberMe(true)
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
                is OnError<*> -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    snack(requireView(),status.msg.toString())
                }
            }
        }
    }

    private fun validateUser() {
        name = binding.etRegisterName.text.toString()
        email = binding.etRegisterEmail.text.toString()
        password = binding.etRegisterPassword.text.toString()
        repassword = binding.etRegisterPasswordAgain.text.toString()

        if (TextUtils.isEmpty(name)) {
            binding.etRegisterName.error = "Enter name"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.error = "Invalid email format"
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
            binding.etRegisterPassword.error = "Enter password"
        } else if (password != repassword) {
            snack(requireView(),"Passwords not match. Try Again.")
        } else {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.register(name, email, password)
        }
    }

}
