package tabom.myhands.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tabom.myhands.domain.user.dto.UserRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "employee_num", unique = true, nullable = false)
    private Integer employeeNum;

    @Column(nullable = false)
    private String name;

    @Column(name = "joined_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "Asia/Seoul" )
    private LocalDateTime joinedAt;

    @Column(name = "avatar_id")
    private Integer avatarId;

    private Integer exp;

    @Column(name = "job_group", nullable = false)
    private Integer jobGroup;

    @Column(nullable = false)
    private String level;


    public static User build(UserRequest.Join request, Department department, String employeeNum, String level) {

        return User.builder()
                .department(department)
                .name(request.getName())
                .id(request.getId())
                .password(request.getPassword())
                .employeeNum(Integer.valueOf(employeeNum))
                .joinedAt(request.getJoinedAt())
                .avatarId(1)
                .exp(0)
                .jobGroup(request.getJobGroup())
                .level(level)
                .build();
    }

}
