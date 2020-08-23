package carloschau.tokengenerator.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ApiIdempotencyService {
    fun isIdempotencyKeyExists(idempotencyKey: String): Boolean{
        return true
    }

    fun addIdempotencyRecord(idempotencyKey: String, httpStatus: HttpStatus, content: ByteArray){

    }

    fun getIdempotencyRecord(idempotencyKey: String){
        
    }
}