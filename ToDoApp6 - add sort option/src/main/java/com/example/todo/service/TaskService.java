package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task addTask(Task task) {
        return repository.save(task);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public void deleteTask(Long id) {
        repository.deleteById(id);
    }

    public Task getTaskById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void toggleTaskCompletion(Long id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.isCompleted());
        repository.save(task);
    }

    public void updateTask(Long id, String newName, LocalDate newDueDate, Task.Priority newPriority) {
        Task task = getTaskById(id);
        task.setName(newName);
        task.setDueDate(newDueDate);
        task.setPriority(newPriority); // set priority
        repository.save(task);
    }

    public List<Task> getAllTasksSorted(String sortBy) {
        return repository.findAll(getSortObject(sortBy));
    }

    private Sort getSortObject(String sortBy) {
        if (sortBy == null) {
            return Sort.unsorted();
        }

        return switch (sortBy) {
            case "dueDateAsc"      -> Sort.by(Sort.Direction.ASC, "dueDate");
            case "dueDateDesc"     -> Sort.by(Sort.Direction.DESC, "dueDate");
            case "priorityAsc"     -> Sort.by(Sort.Direction.ASC, "priority");
            case "priorityDesc"    -> Sort.by(Sort.Direction.DESC, "priority");
            case "completedFirst"  -> Sort.by(Sort.Direction.DESC, "completed");
            case "incompleteFirst" -> Sort.by(Sort.Direction.ASC, "completed");
            default                -> Sort.unsorted(); // prevent NPE for unknown inputs
        };
    }

}
