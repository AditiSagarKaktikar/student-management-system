package com.example.student_management.controller;

import com.example.student_management.dto.PagedResponseDTO;
import com.example.student_management.dto.StudentRequestDTO;
import com.example.student_management.dto.StudentResponseDTO;
import com.example.student_management.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

//    @GetMapping
//    public List<StudentResponseDTO> getAllStudents() {
//        return studentService.getAllStudents();
//    }

    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    public StudentResponseDTO createStudent(@Valid @RequestBody StudentRequestDTO dto) {
        return studentService.createStudent(dto);
    }

    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO dto) {
        return studentService.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
    @GetMapping
    public PagedResponseDTO<StudentResponseDTO> getAllStudents(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return studentService.getStudents(search, page, size, sortBy, direction);
    }
}