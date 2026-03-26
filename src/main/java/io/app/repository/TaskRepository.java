package io.app.repository;

import io.app.model.Task;
import io.app.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("SELECT t FROM Task t JOIN t.parentTask p WHERE p.id=:taskId")
    public List<Task> findTaskByParentId(@Param("taskId") Long taskId);

    @Query("SELECT p FROM Task t JOIN t.parentTask p where t.id=:taskId")
    public List<Task> findDependencyTaskById(@Param("taskId") Long taskId);

    @Query("SELECT t FROM Task t WHERE t.title like %:keyword% OR t.description like %:keyword%")
    public Set<Task> findByKeyword(@Param("keyword") String keyword);

    public List<Task> findByStatusNotIn(Set<TaskStatus> taskStatuses);

}
