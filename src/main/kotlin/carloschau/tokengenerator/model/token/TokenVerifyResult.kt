package carloschau.tokengenerator.model.token

enum class TokenVerifyResult(val reason: String) {
    SUCCESS("Success"),
    EXPIRED("Token expired"),
    SIGNATURE_INVALID("Signature cannot be verified"),
    NOT_FOUND("Token not found"),
    REVOKED("Token is revoked"),
    CONSUMED("Token is already used"),
    UNKNOWN("Unknown")
}