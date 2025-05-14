package com.singidunum.encrypted_drive_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "folder")
public class Folder extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private int folderId;

    private String name;

    @Column(name = "workspace_id")
    private int workspaceId;

    @Column(name = "parent_id")
    private int parentId;
}
