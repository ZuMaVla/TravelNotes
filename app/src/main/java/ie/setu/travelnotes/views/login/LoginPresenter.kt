package ie.setu.travelnotes.views.login

import ie.setu.travelnotes.main.MainApp
import ie.setu.travelnotes.models.user.UserModel

class LoginPresenter(private val view: LoginView) {

    private val app: MainApp = view.app

    fun doLogin(username: String, password: String) {
        val existingUser = app.users.findUserByName(username)

        if (existingUser != null) {
            // User exists, check password
            if (existingUser.password == password) {
                app.currentUser = existingUser
                view.setResultAndFinish()
            } else {
                view.showSnackBar("Invalid password")
                view.clearPasswordField()
            }
        } else {
            // User does not exist, create a new one
            val newUser = UserModel(name = username, password = password)
            app.users.createUser(newUser)
            app.currentUser = newUser // Log in the new user
            view.setResultAndFinish()
        }
    }
}