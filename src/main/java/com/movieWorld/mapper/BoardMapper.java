package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.movieWorld.domain.Board;
import com.movieWorld.dto.response.BoardListDto;

@Mapper
public interface BoardMapper {

	/** 게시글 등록 */
    void insert(Board board);

    /** 게시글 수정 */
    void update(Board board);

    /** 게시글 ID로 삭제 */
    void deleteById(Long id);

    /** 게시글 ID로 조회 */
    Board findById(Long id);

    /** 조회수 1 증가 */
    void increaseViewCount(Long id);
    
    /** 게시글 목록 (작성자명 포함, 최신순) */
    List<BoardListDto> findBoardList();

	/** 검색 조건에 맞는 전체 개수 (페이지네이션용) */
	int countBySearch(@Param("keyword") String keyword);
	
	/** 게시글 목록 (작성자명 포함, 검색·페이징) */
	List<BoardListDto> findBoardList(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
	
}
