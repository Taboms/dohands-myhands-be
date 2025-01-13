package tabom.myhands.domain.quest.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.domain.quest.dto.QuestRequest;
import tabom.myhands.domain.quest.dto.QuestResponse;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.quest.service.QuestService;
import tabom.myhands.domain.quest.service.UserQuestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/quest")
public class QuestController {

    private final ResponseProperties responseProperties;
    private final QuestService questService;
    private final UserQuestService userQuestService;

    @PostMapping
    public ResponseEntity<DtoResponse<Quest>> createQuest(@RequestBody QuestRequest.Create request) {
        Quest quest = questService.createQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.CREATED, responseProperties.getSuccess(), quest));
    }

    @PatchMapping
    public ResponseEntity<DtoResponse<Quest>> updateQuest(@RequestBody QuestRequest.Complete request) {
        Quest quest = questService.updateQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), quest));
    }

    @PostMapping("/job")
    public ResponseEntity<DtoResponse<QuestResponse>> createWeekCountJobQuest(@RequestBody QuestRequest.JobQuest request) {
        Quest weekCountJobQuest = questService.createWeekCountJobQuest(request);
        QuestResponse response = QuestResponse.from(weekCountJobQuest);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.CREATED, responseProperties.getSuccess(), response));
    }

    @GetMapping("/job")
    public ResponseEntity<DtoResponse<QuestResponse>> getWeekCountJobQuest(@RequestParam String departmentName,
                                                                           @RequestParam Integer jobGroup,
                                                                           @RequestParam Integer weekCount) {
        QuestRequest.JobQuest request = new QuestRequest.JobQuest(departmentName, jobGroup, weekCount);
        QuestResponse response = questService.getWeekCountJobQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/job")
    public ResponseEntity<DtoResponse<QuestResponse>> updateWeekCountJobQuest(@RequestBody QuestRequest.UpdateJobQuest request) {
        QuestResponse response = questService.updateWeekCountJobQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping("/leader")
    public ResponseEntity<DtoResponse<QuestResponse>> getLeaderQuest(@RequestParam Integer month,
                                                                           @RequestParam Integer employeeNum,
                                                                           @RequestParam String name,
                                                                           @RequestParam String questName,
                                                                           @RequestParam String grade,
                                                                           @RequestParam Integer expAmount) {
        QuestRequest.LeaderQuest request = new QuestRequest.LeaderQuest(month, employeeNum, name, questName, grade, expAmount);
        QuestResponse response = questService.getLeaderQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @PatchMapping("/leader")
    public ResponseEntity<DtoResponse<QuestResponse>> updateLeaderQuest(@RequestBody QuestRequest.UpdateLeaderQuest request) {
        QuestResponse response = questService.updateLeaderQuest(request);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), response));
    }

    @GetMapping
    public ResponseEntity<DtoResponse<List<QuestResponse>>> getQuests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<QuestResponse> quests = userQuestService.getQuests(userId);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), quests));
    }

    @GetMapping("/completelist")
    public ResponseEntity<DtoResponse<List<QuestResponse>>> getCompletedQuests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<QuestResponse> completedQuest = userQuestService.getCompletedQuest(userId);
        return ResponseEntity.ok(new DtoResponse<>(HttpStatus.OK, responseProperties.getSuccess(), completedQuest));
    }
}
