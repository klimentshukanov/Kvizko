package com.example.kvizko.repository;

import com.example.kvizko.models.views.Najangazhiranikorisnici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NajangazhiranikorisniciRepository extends JpaRepository<Najangazhiranikorisnici, String> {
}
