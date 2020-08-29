package carloschau.tokengenerator.repository.project

import carloschau.tokengenerator.model.dao.project.Project
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : MongoRepository<Project, String>  {
}