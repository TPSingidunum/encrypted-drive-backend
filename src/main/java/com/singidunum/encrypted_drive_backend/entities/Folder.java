package com.singidunum.encrypted_drive_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "folder")
public class Folder extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Integer folderId;

    private String name;

    @Column(name = "workspace_id")
    private Integer workspaceId;

    @Column(name = "parent_id")
    private Integer parentId;

    // Parent
    @ManyToOne()
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonBackReference
    private Folder parentFolder;

    // Children
    @OneToMany(mappedBy = "parentFolder")
    private List<Folder> childrenFolders = new ArrayList<>();

    @OneToMany(mappedBy = "parentFolder")
    private List<Folder> childrenFiles = new ArrayList<>();
}
