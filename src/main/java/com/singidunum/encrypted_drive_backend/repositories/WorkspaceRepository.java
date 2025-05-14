package com.singidunum.encrypted_drive_backend.repositories;

import com.singidunum.encrypted_drive_backend.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {
}
