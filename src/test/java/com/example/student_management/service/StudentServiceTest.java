package com.example.student_management.service;

import com.example.student_management.dto.StudentRequestDTO;
import com.example.student_management.dto.StudentResponseDTO;
import com.example.student_management.entity.Student;
import com.example.student_management.exception.ResourceNotFoundException;
import com.example.student_management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student();
        sampleStudent.setId(1L);
        sampleStudent.setName("Krishna");
        sampleStudent.setEmail("krishna@test.com");
        sampleStudent.setCourse("Computer Science");
    }

    @Test
    void getStudentById_whenStudentExists_returnsStudent() {
        // Arrange: tell the fake repository what to return when asked
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        // Act: call the real method we're testing
        StudentResponseDTO result = studentService.getStudentById(1L);

        // Assert: check the result is what we expect
        assertEquals("Krishna", result.getName());
        assertEquals("krishna@test.com", result.getEmail());
    }

    @Test
    void getStudentById_whenStudentDoesNotExist_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(99L);
        });
    }

    @Test
    void createStudent_savesAndReturnsStudent() {
        StudentRequestDTO requestDto = new StudentRequestDTO();
        requestDto.setName("New Student");
        requestDto.setEmail("new@test.com");
        requestDto.setCourse("Mathematics");

        Student savedEntity = new Student();
        savedEntity.setId(2L);
        savedEntity.setName("New Student");
        savedEntity.setEmail("new@test.com");
        savedEntity.setCourse("Mathematics");

        when(studentRepository.save(any(Student.class))).thenReturn(savedEntity);

        StudentResponseDTO result = studentService.createStudent(requestDto);

        assertEquals(2L, result.getId());
        assertEquals("New Student", result.getName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void deleteStudent_whenStudentDoesNotExist_throwsException() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.deleteStudent(99L);
        });

        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void deleteStudent_whenStudentExists_deletesSuccessfully() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }
}