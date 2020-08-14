package carloschau.tokengenerator.model.dto.request.token

data class CreateToken(
        val tokenGroupUuid: String,
        val type: String?,
        val media: String?
)