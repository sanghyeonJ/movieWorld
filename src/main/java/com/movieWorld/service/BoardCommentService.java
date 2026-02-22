package com.movieWorld.service;

import com.movieWorld.domain.BoardComment;
import com.movieWorld.dto.response.BoardCommentListDto;
import com.movieWorld.mapper.BoardCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentMapper boardCommentMapper;

    public void save(Long userId, Long boardId, String content) {
        BoardComment comment = new BoardComment();
        comment.setBoardId(boardId);
        comment.setUserId(userId);
        comment.setContent(content != null ? content.trim() : "");
        boardCommentMapper.insert(comment);
    }

    public List<BoardCommentListDto> getCommentListByBoardId(Long boardId) {
        if (boardId == null) return List.of();
        return boardCommentMapper.findCommentListByBoardId(boardId);
    }

    public BoardComment findById(Long id) {
        return boardCommentMapper.findById(id);
    }

    @Transactional
    public void deleteById(Long id, Long userId) {
        BoardComment comment = boardCommentMapper.findById(id);
        if (comment == null) throw new IllegalArgumentException("댓글이 없습니다.");
        if (!comment.getUserId().equals(userId)) throw new IllegalArgumentException("본인 댓글만 삭제할 수 있습니다.");
        boardCommentMapper.deleteById(id);
    }
}