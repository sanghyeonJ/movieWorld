package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.BoardComment;
import com.movieWorld.dto.response.BoardCommentListDto;

@Mapper
public interface BoardCommentMapper {

	/** 댓글 등록 */
    void insert(BoardComment comment);

    /** 댓글 ID로 삭제 */
    void deleteById(Long id);

    /** 댓글 ID로 조회 */
    BoardComment findById(Long id);
    
    /** 게시글별 댓글 목록 (작성자명 포함) */
    List<BoardCommentListDto> findCommentListByBoardId(Long boardId);
	
}
