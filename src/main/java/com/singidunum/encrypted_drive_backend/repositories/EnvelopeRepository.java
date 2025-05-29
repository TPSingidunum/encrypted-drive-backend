package com.singidunum.encrypted_drive_backend.repositories;

import com.singidunum.encrypted_drive_backend.entities.Envelope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvelopeRepository extends JpaRepository<Envelope, Integer> {
    Envelope findByFileIdAndUserId(int fileId, int userId);
}