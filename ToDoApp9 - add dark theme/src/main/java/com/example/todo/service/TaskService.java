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

    public List<Task> getFilteredAndSortedTasks(String status, LocalDate dueAfter, LocalDate dueBefore,
                                                String priorityFilter, String sortBy) {
        List<Task> tasks = repository.findAll();

        // Filter by completion status
        if ("complete".equalsIgnoreCase(status)) {
            tasks = tasks.stream().filter(Task::isCompleted).toList();
        } else if ("incomplete".equalsIgnoreCase(status)) {
            tasks = tasks.stream().filter(t -> !t.isCompleted()).toList();
        }

        // Filter by due date
        if (dueAfter != null) {
            tasks = tasks.stream().filter(t -> t.getDueDate() != null && !t.getDueDate().isBefore(dueAfter)).toList();
        }
        if (dueBefore != null) {
            tasks = tasks.stream().filter(t -> t.getDueDate() != null && !t.getDueDate().isAfter(dueBefore)).toList();
        }

        // Filter by priority
        if (priorityFilter != null && !priorityFilter.isBlank()) {
            try {
                Task.Priority priorityEnum = Task.Priority.valueOf(priorityFilter);
                tasks = tasks.stream().filter(t -> priorityEnum.equals(t.getPriority())).toList();
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Sort
        return sortBy != null ? tasks.stream()
                .sorted(getComparator(sortBy))
                .toList() : tasks;
    }

    private Comparator<Task> getComparator(String sortBy) {
        return switch (sortBy) {
            case "dueDateAsc" -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "dueDateDesc" -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.reverseOrder()));
            case "priorityAsc" -> Comparator.comparing(t -> t.getPriority() == null ? 0 : t.getPriority().ordinal());
            case "priorityDesc" -> Comparator.comparing((Task t) -> t.getPriority() == null ? 0 : t.getPriority().ordinal()).reversed();
            case "completedFirst" -> Comparator.comparing(Task::isCompleted).reversed();
            case "incompleteFirst" -> Comparator.comparing(Task::isCompleted);
            default -> (t1, t2) -> 0;
        };
    }


}
