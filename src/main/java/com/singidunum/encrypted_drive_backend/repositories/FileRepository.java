package com.singidunum.encrypted_drive_backend.repositories;

import com.singidunum.encrypted_drive_backend.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer> {
}
