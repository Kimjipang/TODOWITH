package com.example.backend.board.repository;

import com.example.backend.board.domain.Board;
import com.example.backend.board.domain.Category;
import com.example.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

//    @EntityGraph(attributePaths = {"user", "boardTodo"}, type = EntityGraph.EntityGraphType.LOAD)
    Page<Board> findAll(Pageable pageable);

    Page<Board> findAllByCategory(Category category, Pageable pageable);
//    Page<Board> findAllByTitleContaining(String keyword, Pageable pageable);

    Page<Board> findByUser(User user, Pageable pageable);

    Page<Board> findByTitleContainingAndUser(String keyword, User user, Pageable pageable);

    Page<Board> findByTitleContainingAndCategory(String keyword, Category category, Pageable pageable);

    Page<Board> findByTitleContaining(String keyword, Pageable pageable);

    Page<Board> findByContentContainingAndUser(String keyword, User user, Pageable pageable);

    Page<Board> findByContentContainingAndCategory(String keyword, Category category, Pageable pageable);

    Page<Board> findByContentContaining(String keyword, Pageable pageable);

}
