package tabom.myhands.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tabom.myhands.domain.alarm.dto.AlarmResponse;
import tabom.myhands.domain.alarm.entity.Alarm;
import tabom.myhands.domain.alarm.repository.AlarmRepository;
import tabom.myhands.domain.board.entity.Board;
import tabom.myhands.domain.quest.entity.Quest;
import tabom.myhands.domain.user.entity.User;
import tabom.myhands.domain.user.repository.UserRepository;
import tabom.myhands.error.errorcode.UserErrorCode;
import tabom.myhands.error.exception.UserApiException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void deleteRecentAlarm(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        alarmRepository.deleteTodayAlarmsByUser(user,startOfDay);
    }

    @Override
    @Transactional
    public void deleteOldAlarm(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        alarmRepository.deleteOldAlarms(user, startOfDay);
    }

    @Override
    public AlarmResponse.AlarmList getAlarmList(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()-> new UserApiException(UserErrorCode.USER_ID_NOT_FOUND));
        List<AlarmResponse.CreateAlarm> recentList = new ArrayList<>();
        List<AlarmResponse.CreateAlarm> oldList = new ArrayList<>();

        List<Alarm> alarmList = alarmRepository.findAllByUserOrderByCreatedAtDesc(user);

        for(Alarm a : alarmList) {
            boolean lastDay = a.getCreatedAt().isBefore(LocalDate.now().atStartOfDay());
            String createdAt = formatCreatedAt(a.getCreatedAt(), lastDay);
            AlarmResponse.CreateAlarm ca = AlarmResponse.CreateAlarm.build(a, createdAt);

            if(lastDay) {
                oldList.add(ca);
            } else {
                recentList.add(ca);
            }
        }

        return AlarmResponse.AlarmList.build(recentList, oldList);
    }

    @Override
    @Transactional
    public void createBoardAlarm(Board board) {
        List<User> users = userRepository.findAll();

        for(User user : users) {
            Alarm alarm = Alarm.BoardAlarmCreate(user, board);
            alarmRepository.save(alarm);
            if(user.getDeviceToken() != null) {
                sendMessage(user, alarm, false);
            }
        }
    }

    @Override
    @Transactional
    public void createExpAlarm(User user, Quest quest, boolean updateAlarm) {
        Alarm alarm = Alarm.ExpAlarmCreate(user, quest);
        alarmRepository.save(alarm);

        if(user.getDeviceToken() != null) {
            sendMessage(user, alarm, updateAlarm);
        }
    }

    private String formatCreatedAt(LocalDateTime d, boolean lastDay) {
        if(lastDay) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d.");
            return d.format(formatter);
        }

        int diffMin = (int) Duration.between(d, LocalDateTime.now()).toMinutes();

        if(diffMin > 60) {
            return diffMin / 60 + "시간 전";
        }

        if(diffMin > 10) {
            return diffMin / 10 + "0분 전";
        }

        return diffMin + "분 전";
    }

    public void sendMessage(User user, Alarm alarm, boolean updateAlarm) {
        String title = "공지사항";
        String body = alarm.getTitle();

        if(updateAlarm) {
            title = "경험치 변경";
            body = body + "\n" + alarm.getExp() + " do로 변경되었습니다.";
        } else if(!alarm.isCategory()) {
            title = "경험치 획득";
            body = body + "\n" + alarm.getExp() + " do를 획득하셨습니다.";
        }

        try {
            Message message = Message.builder()
                    .setToken(user.getDeviceToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info(title);
            log.info(body);
            log.info(response);
        } catch (FirebaseMessagingException e) {
            log.info(e.toString());
        }
    }

}
