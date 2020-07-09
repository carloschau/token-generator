package carloschau.tokengenerator.model.dto.request.token

//TODO: Maybe add a field `id` as a search option in db
data class RevokeTokenRequest(
        val uuid: String
)