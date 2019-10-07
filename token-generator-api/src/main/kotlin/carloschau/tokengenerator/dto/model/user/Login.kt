package carloschau.tokengenerator.dto.model.user

enum class LoginStatus(val value: Int){
    SUCCESS(0),
    EMAIL_OR_PASSWORD_ERROR(1),
    INACTIVE(2),
    ACCOUNT_LOCKED(3)
}

data class LoginDto(val status : LoginStatus)