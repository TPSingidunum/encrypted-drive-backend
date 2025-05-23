package com.singidunum.encrypted_drive_backend.repositories;

import com.singidunum.encrypted_drive_backend.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findAllByParentIdIsNullAndWorkspaceId(int workspaceId);
    List<File> findAllByParentId(int parentId);
}
