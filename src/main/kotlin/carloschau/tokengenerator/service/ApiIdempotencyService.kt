package carloschau.tokengenerator.service

import carloschau.tokengenerator.model.dao.idempotency.IdempotencyRecord
import carloschau.tokengenerator.repository.idempotency.IdempotencyRecordRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ApiIdempotencyService {

    @Autowired
    lateinit var idempotencyRecordRepository: IdempotencyRecordRepository

    fun isIdempotencyKeyExists(idempotencyKey: String): Boolean{
        return idempotencyRecordRepository.existsById(idempotencyKey)
    }

    fun addIdempotencyRecord(idempotencyKey: String, httpStatus: Int, content: ByteArray){
        idempotencyRecordRepository.insert(IdempotencyRecord(
                idempotencyKey,
                httpStatus,
                content
        ))
    }

    fun getIdempotencyRecord(idempotencyKey: String): IdempotencyRecord? {
        return idempotencyRecordRepository.findByIdOrNull(idempotencyKey)
    }
}