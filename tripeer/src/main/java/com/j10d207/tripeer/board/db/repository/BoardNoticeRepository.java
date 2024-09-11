package com.j10d207.tripeer.board.db.repository;

import com.j10d207.tripeer.board.db.entity.BoardNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardNoticeRepository extends JpaRepository<BoardNoticeEntity, Long> {
}
