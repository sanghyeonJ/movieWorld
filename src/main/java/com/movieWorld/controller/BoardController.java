package com.movieWorld.controller;

import com.movieWorld.config.CustomUserDetails;
import com.movieWorld.domain.Board;
import com.movieWorld.domain.BoardComment;
import com.movieWorld.domain.User;
import com.movieWorld.dto.request.BoardRequest;
import com.movieWorld.dto.response.BoardCommentListDto;
import com.movieWorld.dto.response.BoardListDto;
import com.movieWorld.mapper.BoardCommentMapper;
import com.movieWorld.mapper.BoardMapper;
import com.movieWorld.mapper.UserMapper;
import com.movieWorld.service.BoardCommentService;
import com.movieWorld.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;
    private final BoardCommentMapper boardCommentMapper;
    private final BoardCommentService boardCommentService;

    @GetMapping
    public String list(
    		@RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        String keyword = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        List<BoardListDto> boards = boardService.getBoardList(page, keyword);
        int totalCount = boardService.getTotalCount(keyword);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / BoardService.PAGE_SIZE));

        model.addAttribute("boards", boards);
        model.addAttribute("searchKeyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "boards/list";
    }
    
    /** 글쓰기 폼 */
    @GetMapping("/write")
    public String writeForm(@AuthenticationPrincipal CustomUserDetails user,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        model.addAttribute("boardRequest", new BoardRequest());
        return "boards/write";
    }

    /** 글쓰기 처리 */
    @PostMapping("/write")
    public String write(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute BoardRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return "boards/write";
        }
        boardService.write(user.getUserId(), request);
        redirectAttributes.addFlashAttribute("message", "글이 등록되었습니다.");
        return "redirect:/boards";
    }
    
    /** 게시글 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @AuthenticationPrincipal CustomUserDetails user,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Board board = boardMapper.findById(id);
        if (board == null) {
            redirectAttributes.addFlashAttribute("error", "글이 없습니다.");
            return "redirect:/boards";
        }
        boardMapper.increaseViewCount(id);
        board.setViewCount(board.getViewCount() + 1);

        User author = userMapper.findById(board.getUserId());
        String authorName = author != null ? author.getName() : "알 수 없음";

        List<BoardCommentListDto> comments = boardCommentMapper.findCommentListByBoardId(id);

        model.addAttribute("board", board);
        model.addAttribute("authorName", authorName);
        model.addAttribute("comments", comments);
        model.addAttribute("isOwner", user != null && user.getUserId().equals(board.getUserId()));
        model.addAttribute("currentUserId", user != null ? user.getUserId() : null);
        return "boards/detail";
    }
    
    /** 수정 폼 (본인 글만) */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id,
                          @AuthenticationPrincipal CustomUserDetails user,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        Board board = boardService.findById(id);
        if (board == null) {
            redirectAttributes.addFlashAttribute("error", "글이 없습니다.");
            return "redirect:/boards";
        }
        if (!board.getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "본인 글만 수정할 수 있습니다.");
            return "redirect:/boards/" + id;
        }
        BoardRequest request = new BoardRequest();
        request.setTitle(board.getTitle());
        request.setContent(board.getContent());
        model.addAttribute("boardRequest", request);
        model.addAttribute("boardId", id);
        return "boards/edit";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id,
                      @AuthenticationPrincipal CustomUserDetails user,
                      @Valid @ModelAttribute("boardRequest") BoardRequest request,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "입력값을 확인하세요.");
            return "redirect:/boards/" + id + "/edit";
        }
        try {
            boardService.update(id, user.getUserId(), request);
            redirectAttributes.addFlashAttribute("message", "수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/boards/" + id;
    }
    
    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        try {
            boardService.deleteBoard(id, user.getUserId());
            redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/boards";
    }
    
	
	/** 댓글 등록 */
	@PostMapping("/{boardId}/comments")
	public String addComment(
	        @PathVariable("boardId") Long boardId,
	        @RequestParam(name = "content") String content,
	        @AuthenticationPrincipal CustomUserDetails user,
	        RedirectAttributes redirectAttributes
	) {
	    if (user == null) {
	        redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }
	    if (content == null || content.trim().isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "댓글 내용을 입력하세요.");
	        return "redirect:/boards/" + boardId;
	    }
	    boardCommentService.save(user.getUserId(), boardId, content);
	    redirectAttributes.addFlashAttribute("message", "댓글이 등록되었습니다.");
	    return "redirect:/boards/" + boardId;
	}
	
	/** 댓글 삭제 */
	@PostMapping("/comments/{commentId}/delete")
	public String deleteComment(
	        @PathVariable("commentId") Long commentId,
	        @AuthenticationPrincipal CustomUserDetails user,
	        RedirectAttributes redirectAttributes
	) {
	    if (user == null) {
	        redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
	        return "redirect:/login";
	    }
	    BoardComment comment = boardCommentService.findById(commentId);
	    if (comment == null) {
	        return "redirect:/boards";
	    }
	    try {
	        boardCommentService.deleteById(commentId, user.getUserId());
	        redirectAttributes.addFlashAttribute("message", "댓글이 삭제되었습니다.");
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }
	    return "redirect:/boards/" + comment.getBoardId();
	}
    
}