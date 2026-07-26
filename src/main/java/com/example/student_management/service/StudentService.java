package com.example.student_management.service;

import com.example.student_management.dto.PagedResponseDTO;
import com.example.student_management.dto.StudentRequestDTO;
import com.example.student_management.dto.StudentResponseDTO;
import com.example.student_management.entity.Student;
import com.example.student_management.exception.ResourceNotFoundException;
import com.example.student_management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    private StudentResponseDTO toResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(), student.getName(), student.getEmail(), student.getCourse());
    }

    private Student toEntity(StudentRequestDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setCourse(dto.getCourse());
        return student;
    }

    public PagedResponseDTO<StudentResponseDTO> getStudents(
            String search, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String searchTerm = (search == null) ? "" : search;

        Page<Student> studentPage = studentRepository
                .findByNameContainingIgnoreCaseOrCourseContainingIgnoreCase(searchTerm, searchTerm, pageable);

        return new PagedResponseDTO<>(
                studentPage.getContent().stream().map(this::toResponseDTO).collect(Collectors.toList()),
                studentPage.getNumber(),
                studentPage.getTotalPages(),
                studentPage.getTotalElements(),
                studentPage.isLast()
        );
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
        return toResponseDTO(student);
    }

    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        Student student = toEntity(dto);
        Student saved = studentRepository.save(student);
        return toResponseDTO(saved);
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setCourse(dto.getCourse());
        Student updated = studentRepository.save(existing);
        return toResponseDTO(updated);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id " + id);
        }
        studentRepository.deleteById(id);
    }
}