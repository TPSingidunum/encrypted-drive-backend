package com.singidunum.encrypted_drive_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "file")
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    private String name;

    private String path;

    @Column(name = "workspace_id")
    private Integer workspaceId;

    @Column(name = "parent_id")
    private Integer parentId;

    @ManyToOne()
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonBackReference
    private Folder parentFolder;

    @OneToMany(mappedBy = "file")
    @JsonManagedReference
    private List<Envelope> envelopes = new ArrayList<>();
}
