package ie.setu.travelnotes.models.user

interface UserStore {
    fun findAllUsers(): List<UserModel>
    fun findUserById(id: Long): UserModel?
    fun findUserByName(name: String): UserModel?
    fun createUser(user: UserModel)
    fun updateUser(user: UserModel)
    fun deleteUser(user: UserModel)
}