package com.singidunum.encrypted_drive_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    private String username;

    private String email;

    private String password;

    @Lob
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    /* Relations */
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Workspace> workspaces = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Envelope> envelopes = new ArrayList<>();
}
