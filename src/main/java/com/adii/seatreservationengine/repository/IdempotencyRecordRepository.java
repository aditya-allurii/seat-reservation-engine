package com.adii.seatreservationengine.repository;

import com.adii.seatreservationengine.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,Long> {

    Optional<IdempotencyRecord> findByKey(String key);
}
