package com.example.todo.controller;

import com.example.todo.entity.Task;
import com.example.todo.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(@RequestParam(name = "sortBy", required = false) String sortBy, Model model) {
        List<Task> tasks = service.getAllTasksSorted(sortBy);
        System.out.println("Tasks loaded: " + tasks.size()); // For debugging
        model.addAttribute("tasks", tasks);
        model.addAttribute("task", new Task());
        model.addAttribute("sortBy", sortBy);  // For UI selection
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
