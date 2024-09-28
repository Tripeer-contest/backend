package com.j10d207.tripeer.board.db.repository;

import com.j10d207.tripeer.board.db.entity.BoardHelpdeskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHelpdeskRepository extends JpaRepository <BoardHelpdeskEntity, Long> {

    Page<BoardHelpdeskEntity> findByUser_UserId(long userId, Pageable pageable);
}
