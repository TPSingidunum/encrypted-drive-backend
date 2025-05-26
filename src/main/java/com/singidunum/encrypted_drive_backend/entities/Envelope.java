package com.singidunum.encrypted_drive_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
@Entity(name = "envelope")
public class Envelope extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "envelope_id")
    private Integer envelopeId;

    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "user_id")
    private Integer userId;

    private String key;
    private String iv;

    // Relations

    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference
    private User user;

    @ManyToOne()
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    @JsonBackReference
    private File file;
}
