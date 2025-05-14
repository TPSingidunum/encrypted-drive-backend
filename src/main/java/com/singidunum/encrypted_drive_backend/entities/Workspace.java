package com.singidunum.encrypted_drive_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "workspace")
public class Workspace extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private int workspaceId;

    private String name;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
