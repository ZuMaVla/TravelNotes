package ie.setu.travelnotes.views.login

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ie.setu.travelnotes.databinding.ActivityLoginBinding
import ie.setu.travelnotes.main.MainApp

class LoginView : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp
        presenter = LoginPresenter(this)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameField.text.toString()
            val password = binding.passwordField.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                presenter.doLogin(username, password)
            } else {
                showSnackBar("Please enter a username and password")
            }
        }
    }

    fun showSnackBar(message: String) {
        hideKeyboard()
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    fun setResultAndFinish() {
        setResult(RESULT_OK)
        finish()
    }

    fun clearPasswordField() {
        binding.passwordField.text.clear()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}