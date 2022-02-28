package xyz.scoca.helpark.ui.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.scoca.helpark.R
import xyz.scoca.helpark.data.local.ClientPreferences
import xyz.scoca.helpark.databinding.FragmentLoginBinding
import xyz.scoca.helpark.model.status.OnError
import xyz.scoca.helpark.model.status.OnSuccess
import xyz.scoca.helpark.util.extension.snack


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var email: String = ""
    private var password: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = LoginViewModel()

        initObservers()
        initListeners()

        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.btnLoginToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.tvContinueWithoutLogin.setOnClickListener{
            ClientPreferences(requireContext()).setRememberMe(false)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is OnError<*> -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    snack(requireView(),status.msg.toString())
                }
            }
        }
    }

    private fun login() {
        email = binding.etLoginEmail.text.toString()
        password = binding.etLoginPassword.text.toString()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.login(email, password)
    }

    private fun initListeners() {
        binding.etLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }

        })

        binding.etLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }
        })
    }

    private fun checkFields() {
        if (!binding.etLoginEmail.text.isNullOrEmpty() && !binding.etLoginPassword.text.isNullOrEmpty()) {
            binding.btnLogin.isEnabled = true
            binding.btnLogin.alpha = 1F
        } else {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.alpha = 0.2F
        }
    }
}