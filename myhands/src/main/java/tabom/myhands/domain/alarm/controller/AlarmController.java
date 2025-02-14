package tabom.myhands.domain.alarm.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tabom.myhands.common.properties.ResponseProperties;
import tabom.myhands.common.response.DtoResponse;
import tabom.myhands.common.response.MessageResponse;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.alarm.service.AlarmService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {
    private final ResponseProperties responseProperties;
    private final AlarmService alarmService;

    @DeleteMapping("/recent")
    public ResponseEntity<MessageResponse> deleteRecent(HttpServletRequest request) {
        alarmService.deleteRecentAlarm((Long) request.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @DeleteMapping("/old")
    public ResponseEntity<MessageResponse> deleteOld(HttpServletRequest request) {
        alarmService.deleteOldAlarm((Long) request.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of(HttpStatus.OK, responseProperties.getSuccess()));
    }

    @GetMapping("")
    public ResponseEntity<DtoResponse<AlarmResponse.AlarmList>> list(HttpServletRequest request) {
        AlarmResponse.AlarmList response = alarmService.getAlarmList((Long) request.getAttribute("userId"));

        if(response == null) {
            ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getFail(),null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DtoResponse.of(HttpStatus.OK, responseProperties.getSuccess(),response));
    }

}
