package edu.univ.erp.service.student;

import edu.univ.erp.data.repository.EnrollmentRepository;
import edu.univ.erp.data.repository.GradeRepository;
import edu.univ.erp.data.repository.StudentRepository;
import edu.univ.erp.data.repository.UserRepository;
import edu.univ.erp.domain.StudentCourseOption;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.TranscriptRow;
import edu.univ.erp.domain.UserAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranscriptService {
    public static class TranscriptData {
        public UserAccount account;
        public StudentProfile studentProfile;
        public List<TranscriptRow> rows = new ArrayList<>();
    }

    private final EnrollmentRepository enrollmentRepository;
    private final GradeService gradeService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public TranscriptService() {
        this(new EnrollmentRepository(), new GradeService(), new StudentRepository(), new UserRepository());
    }

    public TranscriptService(EnrollmentRepository enrollmentRepository,
                             GradeService gradeService,
                             StudentRepository studentRepository,
                             UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.gradeService = gradeService;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public TranscriptData buildTranscript(int studentId) throws Exception {
        TranscriptData data = new TranscriptData();
        data.account = userRepository.findById(studentId).orElse(null);
        data.studentProfile = studentRepository.findByUserId(studentId).orElse(null);

        List<StudentCourseOption> options = enrollmentRepository.findCourseOptions(studentId);
        for (StudentCourseOption option : options) {
            Optional<GradeService.GradeSummary> summaryOpt = gradeService.loadGrades(studentId, option.getSectionId());
            double finalPercentage = 0.0;
            String letterGrade = "N/A";
            if (summaryOpt.isPresent()) {
                GradeService.GradeSummary summary = summaryOpt.get();
                if (summary.totalWeight > 0) {
                    finalPercentage = summary.weightedScore;
                    letterGrade = gradeService.calculateLetterGrade(finalPercentage);
                }
            }
            data.rows.add(new TranscriptRow(
                    option.getSectionCode(),
                    option.getCourseTitle(),
                    finalPercentage,
                    letterGrade
            ));
        }
        return data;
    }
}

