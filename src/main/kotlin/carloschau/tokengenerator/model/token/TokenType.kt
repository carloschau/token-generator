package carloschau.tokengenerator.model.token

enum class TokenType(val value: String) {
    Text("text"),
    QRCode("qr");

    companion object{
        private val map = TokenType.values().associateBy(TokenType::value)
        fun fromValue(value: String) = map[value]
    }
}