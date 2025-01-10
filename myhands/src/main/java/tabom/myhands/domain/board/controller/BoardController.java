package tabom.myhands.domain.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.board.dto.BoardRequest;
import tabom.myhands.domain.board.dto.BoardResponse;
import tabom.myhands.domain.board.service.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final ResponseProperties responseProperties;

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> create(HttpServletRequest request, @RequestBody BoardRequest.Create requestDto) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        boardService.create(userId, isAdmin, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }

    @PatchMapping("/edit")
    public ResponseEntity<MessageResponse> edit(HttpServletRequest request, @RequestBody BoardRequest.Edit requestDto) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        boardService.edit(userId, isAdmin, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponse> delete(HttpServletRequest request, @RequestParam Long boardId){
        boolean isAdmin = (boolean) request.getAttribute("isAdmin");
        boardService.delete(isAdmin, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of(HttpStatus.CREATED, responseProperties.getSuccess()));
    }

    @GetMapping("/detail")
    public ResponseEntity<DtoResponse<BoardResponse.Detail>> detail(@RequestParam Long boardId){
        BoardResponse.Detail response = boardService.detail(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(),response));
    }
}
