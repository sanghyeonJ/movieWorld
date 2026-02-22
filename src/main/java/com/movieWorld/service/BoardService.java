package com.movieWorld.service;

import com.movieWorld.domain.Board;
import com.movieWorld.dto.request.BoardRequest;
import com.movieWorld.dto.response.BoardListDto;
import com.movieWorld.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    public static final int PAGE_SIZE = 10;

    public List<BoardListDto> getBoardList(int page, String keyword) {
        int offset = (page - 1) * PAGE_SIZE;
        return boardMapper.findBoardList(keyword, offset, PAGE_SIZE);
    }

    public int getTotalCount(String keyword) {
        return boardMapper.countBySearch(keyword);
    }
    
    /** 게시글 작성 (로그인 사용자) */
    public void write(Long userId, BoardRequest request) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(request.getTitle().trim());
        board.setContent(request.getContent().trim());
        board.setViewCount(0);
        boardMapper.insert(board);
    }
    
    /**
     * 게시글 ID로 조회 (상세/수정 폼용)
     */
    public Board findById(Long id) {
        return boardMapper.findById(id);
    }
    
    /** 수정 (본인만 가능) */
    public void update(Long id, Long userId, BoardRequest request) {
        Board board = boardMapper.findById(id);
        if (board == null) throw new IllegalArgumentException("글이 없습니다.");
        if (!board.getUserId().equals(userId)) throw new IllegalArgumentException("본인 글만 수정할 수 있습니다.");
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        boardMapper.update(board);
    }
    
    /**
     * 게시글 삭제 (본인만 가능)
     * @throws IllegalArgumentException 작성자가 아닐 때
     */
    @Transactional
    public void deleteBoard(Long id, Long userId) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        if (!board.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 글만 삭제할 수 있습니다.");
        }
        boardMapper.deleteById(id);
    }
    
}