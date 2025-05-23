package com.singidunum.encrypted_drive_backend.repositories;

import com.singidunum.encrypted_drive_backend.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findAllByParentIdIsNullAndWorkspaceId(int workspaceId);
    List<Folder> findAllByParentId(int parentId);
}
