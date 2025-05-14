package com.singidunum.encrypted_drive_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "file")
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    private String name;

    private String path;

    @Column(name = "workspace_id")
    private int workspaceId;

    @Column(name = "parent_id")
    private int parentId;
}
