package carloschau.tokengenerator.repository.idempotency

import carloschau.tokengenerator.model.dao.idempotency.IdempotencyRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface IdempotencyRecordRepository : MongoRepository<IdempotencyRecord,String> {

}