package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    MESSAGE_NULL("Cannot send a null message"),
    MESSAGE_EMPTY("Cannot send a empty message"),
    MESSAGE_DATE_NULL("Cannot send a message with a null date"),
    DISCUSSION_NOT_FOUND("Discussion not found"),
    STUDENT_DID_NOT_ANSWER_QUESTION("Student did not answered this question"),
    STUDENT_DOESNT_HAVE_PERMISSION("Student does not have permission to see a discussion clarification without " +
            "answering the corresponding question"),
    STUDENT_IS_NOT_TOURNEY_CREATOR("Only the Tourney %d creator can perform this action"),
    TEACHER_CANNOT_SEE_TEACHER_CLARIFICATION("Permission only for student, only user can see teacher clarification"),
    TEACHER_DID_NOT_CLARIFIED("Theacher did not clarified the discussion yet"),

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    TOPICS_NOT_FROM_SAME_COURSE("Topics are not from the same course"),

    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    INVALID_NUMBER_OF_OPTIONS("Questions must have 4 options"),
    QUESTION_MISSING_TITLE_OR_CONTENT("Questions need a title and content"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    STUDENT_QUESTION_NOT_FOUND("Student Question not found with id %d"),
    QUESTION_MISSING_JUSTIFICATION("Missing a justification in order to reject question"),
    QUESTION_NOT_PENDING("Question status not pending"),
    USER_WITHOUT_SUBMITTED_QUESTIONS("This user doesn't have any submitted questions"),
    QUESTION_ALREADY_APPROVED("You can't delete an approved question"),
    QUESTION_MISSING_OPTIONS("You need to fill all the option fields"),
  
    ACCESS_DENIED("You do not have permission to view this resource"),

    TOURNEY_NOT_CONSISTENT("Field %s of tourney is not consistent."),
    TOURNEY_AVAILABLEDATE_BIGGER_THAN_CONCLUSIONDATE("Available date is bigger than Conclusion date."),
    TOURNEY_DATE_WRONG_FORMAT("Date format not according to the expected."),
    TOURNEY_NOT_FOUND("Tourney not found"),
    TOURNEY_CLOSED("Tourney closed"),
    STUDENT_ALREADY_ENROLLED("Student already enroled at tourney"),

    USER_NOT_STUDENT("The user is not a student"),
    STUDENT_CANT_ACCESS_COURSE_EXECUTION("Student is not enrolled in the course execution: %s"),

    CANNOT_OPEN_FILE("Cannot open file");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}