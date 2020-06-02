package carloschau.tokengenerator.model.token

enum class TokenType(val value: String) {
    TEXT("text"),
    QR_CODE("qr");

    companion object{
        private val map = TokenType.values().associateBy(TokenType::value)
        fun fromValue(value: String) = map[value]
    }
}