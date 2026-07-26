package com.example.student_management.repository;

import com.example.student_management.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByNameContainingIgnoreCaseOrCourseContainingIgnoreCase(
            String name, String course, Pageable pageable);
}