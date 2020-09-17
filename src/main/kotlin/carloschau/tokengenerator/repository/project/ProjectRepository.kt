package carloschau.tokengenerator.repository.project

import carloschau.tokengenerator.model.dao.project.Member
import carloschau.tokengenerator.model.dao.project.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : MongoRepository<Project, String>, ProjectRepositoryCustom {
    fun deleteByName(name: String) : Long
    fun findByName(name: String): Project?
    fun existsByName(name: String) : Boolean
    fun findByIdIn(ids: List<String>, pageable: Pageable) : List<Project>
}

interface ProjectRepositoryCustom {
    fun updateByName(name: String, updateMap : Map<String, Any>)
    fun pushMember(name: String, member: Member)
}

class ProjectRepositoryCustomImpl : ProjectRepositoryCustom {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    override fun updateByName(name: String, updateMap : Map<String, Any>) {
        val query = query(where("name").`is`(name))
        val update = Update()
        updateMap.forEach { (key, value) ->
            update.set(key, value)
        }
        mongoTemplate.updateFirst(query, update, Project::class.java)
    }

    override fun pushMember(name: String, member: Member) {
        val query = query(where("name").`is`(name))
        val update = Update().push("member", member)
        mongoTemplate.updateFirst(query, update, Project::class.java)
    }
}