package com.example.todo.controller;

import com.example.todo.entity.Task;
import com.example.todo.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
            @RequestParam(required = false) String priorityFilter,
            Model model
    ) {
        List<Task> tasks = service.getFilteredAndSortedTasks(status, dueAfter, dueBefore, priorityFilter, sortBy);

        model.addAttribute("tasks", tasks);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("status", status);
        model.addAttribute("dueAfter", dueAfter);
        model.addAttribute("dueBefore", dueBefore);
        model.addAttribute("priorityFilter", priorityFilter);
        model.addAttribute("task", new Task());

        return "index";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task) {
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            return "redirect:/"; // skip insertion for empty names
        }

        service.addTask(task);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCompletion(@PathVariable Long id) {
        service.toggleTaskCompletion(id);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = service.getTaskById(id);
        model.addAttribute("task", task);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask) {
        service.updateTask(id, updatedTask.getName(), updatedTask.getDueDate(), updatedTask.getPriority());
        return "redirect:/";
    }
}
