package carloschau.tokengenerator.controller

data class  CreateTokenGroupRequest(val name: String = "")

data class CreateUserRequest(val username: String = "", val email : String = "", val password : String = "")