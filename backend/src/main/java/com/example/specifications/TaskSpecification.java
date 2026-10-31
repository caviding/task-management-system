package com.example.specifications;


import com.example.Enum.TaskStatus;
import com.example.entity.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> hasTitle(String title) {
        return (root, query, cb) -> cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasUserId(Long user_id) {
        return (root, query, cb) -> cb.equal(root.get("userId"), user_id);
    }
}
