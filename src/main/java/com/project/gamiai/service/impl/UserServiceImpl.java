package com.project.gamiai.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.gamiai.domain.Leaderboard;
import com.project.gamiai.domain.Problem;
import com.project.gamiai.domain.Submission;
import com.project.gamiai.domain.User;
import com.project.gamiai.dto.request.SigninRequest;
import com.project.gamiai.dto.request.SignupRequest;
import com.project.gamiai.dto.response.AuthResponse;
import com.project.gamiai.dto.response.CompletedLessonDto;
import com.project.gamiai.dto.response.DashboardResponseDto;
import com.project.gamiai.dto.response.LeaderboardEntryDto;
import com.project.gamiai.dto.response.OverallStatsDto;
import com.project.gamiai.dto.response.PerformanceDto;
import com.project.gamiai.dto.response.SolvedProblemDto;
import com.project.gamiai.dto.response.UserInfoResponseDto;
import com.project.gamiai.exception.InvalidCredentialsException;
import com.project.gamiai.exception.UserAlreadyExistsException;
import com.project.gamiai.exception.UserNotFoundException;
import com.project.gamiai.repository.LeaderboardRepository;
import com.project.gamiai.repository.ProblemRepository;
import com.project.gamiai.repository.SubmissionRepository;
import com.project.gamiai.repository.UserProgressRepository;
import com.project.gamiai.repository.UserRepository;
import com.project.gamiai.security.JwtUtil;
import com.project.gamiai.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.project.gamiai.domain.Lesson;
import com.project.gamiai.domain.LessonProgress;
import com.project.gamiai.repository.LessonProgressRepository;
import com.project.gamiai.repository.LessonRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired private ProblemRepository problemRepository;
    @Autowired private LessonRepository lessonRepository; // Add this
    @Autowired private SubmissionRepository submissionRepository;
    @Autowired private LessonProgressRepository lessonProgressRepository; // Add this
    @Autowired private UserProgressRepository userProgressRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // When generating a new refresh token (e.g., on login or refresh)
        String refreshToken = UUID.randomUUID().toString();
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        user.setLevel(request.getLevel());
        userRepository.save(user);

        // --- Add this block to create the leaderboard entry ---
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setUserId(user.getId());
        leaderboard.setLevel(request.getLevel());
        leaderboard.setXp(0);
        leaderboard.setProblemsSolved(0);
        leaderboard.setLessonsCompleted(0);
        leaderboardRepository.save(leaderboard);
        // --- End of block ---

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, refreshToken, user.getId(), request.getLevel());
    }

    @Override
    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Username or email not found!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password does not match!");
        }
        String refreshToken = UUID.randomUUID().toString();
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30 days

        user.setRefreshToken(refreshToken); // Generate new refresh token on login
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, refreshToken, user.getId(), user.getLevel());
    }

    @Override
    public void logout(String refreshToken) {
        // Find the user by refresh token and invalidate it
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token!"));

        // Check if refresh token is expired
        if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().before(new Date())) {
            throw new InvalidCredentialsException("Refresh token expired!");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = UUID.randomUUID().toString();
        Date newRefreshTokenExpiry = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);

        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(newRefreshTokenExpiry);
        userRepository.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getId(), user.getLevel());
    }

    @Override
    public DashboardResponseDto getDashboardData(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        DashboardResponseDto response = new DashboardResponseDto();
        response.setUserInfo(getUserInfo(user));
        response.setOverallStats(getOverallStats(userId));
        response.setLeaderboard(getLeaderboard());
        response.setLessonHistory(getLessonHistory(userId));
        response.setProblemHistory(getProblemHistory(userId));

        return response;
    }

    private UserInfoResponseDto getUserInfo(User user) {
        Leaderboard leaderboard = leaderboardRepository.findByUserId(user.getId()).orElse(new Leaderboard());
        
        // Using a native query with RANK() for efficiency
        String q = "SELECT rank FROM (SELECT user_id, RANK() OVER (ORDER BY xp DESC) as rank FROM leaderboard) as ranked_users WHERE user_id = :userId";
        Long rank = ((Number) entityManager.createNativeQuery(q).setParameter("userId", user.getId()).getSingleResult()).longValue();

        UserInfoResponseDto userInfo = new UserInfoResponseDto();
        userInfo.setUsername(user.getUsername());
        userInfo.setLevel(leaderboard.getLevel());
        userInfo.setXp(leaderboard.getXp());
        userInfo.setRank(rank);
        return userInfo;
    }

    private OverallStatsDto getOverallStats(Integer userId) {
        OverallStatsDto stats = new OverallStatsDto();
        
        long totalProblems = problemRepository.count();
        long solvedProblems = userProgressRepository.countByUserIdAndSolved(userId, true);
        stats.setProblemsSolved(solvedProblems + "/" + totalProblems);

        long totalSubmissions = submissionRepository.countByUserId(userId);
        if (totalSubmissions > 0) {
            long acceptedSubmissions = submissionRepository.countByUserIdAndStatus(userId, "Accepted");
            double rate = ((double) acceptedSubmissions / totalSubmissions) * 100;
            stats.setAcceptanceRate(new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP) + "%");
        } else {
            stats.setAcceptanceRate("0.00%");
        }
        stats.setTotalSubmissions(totalSubmissions);

        // Placeholder for performance beats as it's a complex calculation
        PerformanceDto performance = new PerformanceDto();
        performance.setTime("63.61%");
        performance.setSpace("63.99%");
        performance.setOverall("57.67%");
        stats.setPerformanceBeats(performance);

        stats.setAttempting(userProgressRepository.countByUserIdAndSolved(userId, false));
        
        Map<String, Long> solvedByDifficulty = problemRepository.findSolvedCountByDifficulty(userId);
        Map<String, Long> totalByDifficulty = problemRepository.findTotalCountByDifficulty();
        stats.setByDifficulty(
            totalByDifficulty.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> solvedByDifficulty.getOrDefault(e.getKey(), 0L) + "/" + e.getValue()
            ))
        );

        return stats;
    }

    private List<LeaderboardEntryDto> getLeaderboard() {
        return leaderboardRepository.findTop5ByOrderByXpDesc().stream().map(lb -> {
            User user = userRepository.findById(lb.getUserId()).orElse(new User());
            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setUsername(user.getUsername());
            dto.setLevel(lb.getLevel());
            dto.setXp(lb.getXp());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<CompletedLessonDto> getLessonHistory(Integer userId) {
        return lessonProgressRepository.findByUserIdAndCompletedOrderByCompletedAtDesc(userId, true).stream()
            .map(lessonProgress -> {
                Lesson lesson = lessonRepository.findById(lessonProgress.getLessonId()).orElse(new Lesson());
                CompletedLessonDto dto = new CompletedLessonDto();
                dto.setTitle(lesson.getTitle());
                dto.setDifficulty(lesson.getDifficulty());
                dto.setCompletedAt(lessonProgress.getCompletedAt());
                return dto;
            }).collect(Collectors.toList());
    }

    private List<SolvedProblemDto> getProblemHistory(Integer userId) {
        return submissionRepository.findByUserIdAndStatusOrderBySubmittedAtDesc(userId, "Accepted").stream()
            .collect(Collectors.groupingBy(Submission::getProblemId, Collectors.minBy(java.util.Comparator.comparing(Submission::getRuntime))))
            .values().stream().map(optSub -> optSub.orElse(null))
            .filter(java.util.Objects::nonNull)
            .map(sub -> {
                Problem problem = problemRepository.findById(sub.getProblemId()).orElse(new Problem());
                SolvedProblemDto dto = new SolvedProblemDto();
                dto.setTitle(problem.getTitle());
                dto.setDifficulty(problem.getDifficulty());
                dto.setStatus(sub.getStatus());
                dto.setRuntime(sub.getRuntime());
                dto.setSubmittedAt(sub.getSubmittedAt());
                return dto;
            }).collect(Collectors.toList());
    }
}