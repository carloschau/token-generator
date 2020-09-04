package carloschau.tokengenerator.repository.project

import carloschau.tokengenerator.model.dao.project.Project
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : MongoRepository<Project, String>  {
    fun deleteByName(name: String) : Long
    fun findByName(name: String): Project?
    fun findAllByMember_UserId(userId: String, pageable: Pageable): List<Project>
}