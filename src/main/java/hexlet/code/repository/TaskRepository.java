package hexlet.code.repository;

import hexlet.code.model.QTask;
import hexlet.code.model.Task;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends
        CrudRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {

    @Override
    default void customize(QuerydslBindings bindings, QTask task) {
        bindings.bind(task.taskStatus.id).first((path, value) -> path.eq(value));
        bindings.bind(task.executor.id).first((path, value) -> path.eq(value));
        bindings.bind(task.author.id).first((path, value) -> path.eq(value));
        bindings.bind(task.labels.any().id).first((path, value) -> path.eq(value));
        bindings.excluding(task.id, task.name, task.description, task.createdAt);
    }
}
