import axios from 'axios';
import Store from '@/store';
import Question from '@/models/management/Question';
import { Quiz } from '@/models/management/Quiz';
import Course from '@/models/user/Course';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import StudentStats from '@/models/statement/StudentStats';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import Topic from '@/models/management/Topic';
import { Student } from '@/models/management/Student';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import Tourney from '@/models/tourney/Tourney';
import TourneyStats from '@/models/tourney/TourneyStats';
import Discussion from '@/models/statement/Discussion';
import Message from '@/models/statement/Message';

import DiscussionStats from '@/models/statement/DiscussionStats';

import StudentQuestionStats from '@/models/submissions/StudentQuestionStats';


const httpClient = axios.create();
httpClient.defaults.timeout = 10000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  config => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  error => Promise.reject(error)
);

export default class RemoteServices {
  static async fenixLogin(code: string): Promise<AuthDto> {
    return httpClient
      .get(`/auth/fenix?code=${code}`)
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoStudentLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/student')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoTeacherLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/teacher')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoAdminLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/admin')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`
      )
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseQuestions(): Promise<Blob> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`,
        {
          responseType: 'blob'
        }
      )
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/`,
        question
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put(`/questions/${question.id}`, question)
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuestion(questionId: number) {
    return httpClient.delete(`/questions/${questionId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post(`/questions/${questionId}/set-status`, status, {})
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/questions/${questionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put(`/questions/${questionId}/topics`, topics);
  }

  static async getTopics(): Promise<Topic[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`
      )
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateStatementQuiz(params: object): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`,
        params
      )
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`
      )
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizByQRCode(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/byqrcode`)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportQuiz(quizId: number): Promise<Blob> {
    return httpClient
      .get(`/quizzes/${quizId}/export`, {
        responseType: 'blob'
      })
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startQuiz(quizId: number) {
    return httpClient.get(`/quizzes/${quizId}/start`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async submitAnswer(quizId: number, answer: StatementAnswer) {
    return httpClient
      .post(`/quizzes/${quizId}/submit`, answer)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async concludeQuiz(
    quizId: number
  ): Promise<StatementCorrectAnswer[] | void> {
    return httpClient
      .get(`/quizzes/${quizId}/conclude`)
      .then(response => {
        if (response.data) {
          return response.data.map((answer: any) => {
            return new StatementCorrectAnswer(answer);
          });
        }
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/topics/`,
        topic
      )
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put(`/topics/${topic.id}`, topic)
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTopic(topic: Topic) {
    return httpClient.delete(`/topics/${topic.id}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`
      )
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete(`/quizzes/${quizId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get(`/quizzes/${quizId}`)
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizAnswers(quizId: number): Promise<QuizAnswers> {
    return httpClient
      .get(`/quizzes/${quizId}/answers`)
      .then(response => {
        return new QuizAnswers(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put(`/quizzes/${quiz.id}`, quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`,
          quiz
        )
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async getCourseStudents(course: Course) {
    return httpClient
      .get(`/executions/${course.courseExecutionId}/students`)
      .then(response => {
        return response.data.map((student: any) => {
          return new Student(student);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put(`/assessments/${assessment.id}`, assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`,
          assessment
        )
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post(`/assessments/${assessmentId}/set-status`, status, {
        headers: {
          'Content-Type': 'text/html'
        }
      })
      .then(response => {
        return new Assessment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getCourses(): Promise<Course[]> {
    return httpClient
      .get('/courses/executions')
      .then(response => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async activateCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/activate', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createExternalCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/external', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteCourse(courseExecutionId: number | undefined) {
    return httpClient
      .delete(`/executions/${courseExecutionId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getStudentQuestions(): Promise<StudentQuestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentQuestions/status`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new StudentQuestion(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async submitQuestion(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentQuestions`,
        studentQuestion
      )
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async resubmitQuestion(
    questionId: number | undefined,
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .put(`/studentQuestions/${questionId}/resubmit`, studentQuestion)
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static deleteStudentQuestion(questionId: number) {
    return httpClient
      .delete(`/studentQuestions/${questionId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async approveQuestion(
    questionId: number | undefined,
    justification: String
  ): Promise<StudentQuestion> {
    return httpClient
      .put(`/studentQuestions/${questionId}/approve`, justification)
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async rejectQuestion(
    questionId: number | undefined,
    justification: String
  ): Promise<StudentQuestion> {
    return httpClient
      .put(`/studentQuestions/${questionId}/reject`, justification)
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getStudentQuestionDashboard(): Promise<StudentQuestionStats> {
    return httpClient
      .get('/studentQuestions/dashboard')
      .then(response => {
          return new StudentQuestionStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async toggleStudentQuestionPrivacy(
    privacy: String
  ) : Promise<boolean> {
    return httpClient
      .put(`/student/studentQuestions/${privacy}`)
      .then(response => response.data)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTourney(tourney: Tourney) {
    return httpClient
      .post('/tourneys', tourney)
      .then(response => {
        return new Tourney(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }



  static async submitMessage(
      discussionId: number | undefined,
      message: Message
  ) {
    return httpClient
      .post(
        `/quizAnswer/${discussionId}/${Store.getters.getUser.username}`,
        message
      )
      .then(response => {
        return new Message(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTourneysDashboard(): Promise<TourneyStats[]> {
    return httpClient
      .get('/tourneys/dashboard')
      .then(response => {
        return response.data.map((tourneyStats: any) => {
          return new TourneyStats(tourneyStats);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createDiscussion(quizAnswerId: number | undefined) {
    return httpClient
      .post(
        `/quizAnswer/createDisscussion/${Store.getters.getCurrentCourse.courseId}/${Store.getters.getUser.username}/` +
          quizAnswerId
      )
      .then(response => {
        return new Discussion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeDiscussionDashboardPrivacy() {
    return httpClient
      .post(`/discussion/${Store.getters.getUser.username}/dashboard`)
      .then(response => {
        return response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDiscussionDashboardPrivacy() {
    return httpClient
      .get(`/discussion/dashboard/${Store.getters.getUser.username}`)
      .then(response => {
        return response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDiscussionStats() {
    return httpClient
      .get(
        `/discussion/${Store.getters.getCurrentCourse.courseId}/${Store.getters.getUser.username}/dashboard`
      )
      .then(response => {
        return new DiscussionStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPendingQuestions(): Promise<StudentQuestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentQuestions`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new StudentQuestion(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }


  static async makeQuestionAvailable(
    questionId: number | undefined
  ): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questionsFromStudents`,
        questionId
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveApprovedQuestionChanges(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .put(
        `/studentQuestions/${studentQuestion.id}/editApprovedQuestion`,
        studentQuestion
      )
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }


  static async getTourneys() {
    return httpClient
      .get('/tourneys/open')
      .then(response => {
        return response.data.map((tourney: any) => {
          return new Tourney(tourney);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDiscussions() {
    return httpClient
      .get(`/discussion/${Store.getters.getCurrentCourse.courseId}`)
      .then(response => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDiscussionsStudent() {
    return httpClient
      .get(
        `/discussion/${Store.getters.getCurrentCourse.courseId}/${Store.getters.getUser.username}`
      )
      .then(response => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPublicDiscussionsQuizz(quizAnswerId: number | undefined) {
    return httpClient
      .get(
        `/discussion/public/${Store.getters.getCurrentCourse.courseId}/${quizAnswerId}`
      )
      .then(response => {
        return response.data.map((discussion: any) => {
          return new Discussion(discussion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async seeMessagesDiscussion(discussionId: number | undefined) {
    return httpClient
      .get(`/quizAnswer/${discussionId}`)
      .then(response => {
        return response.data.map((message: any) => {
          return new Message(message);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async makeDiscussionPublic(discussionId: number | undefined) {
    console.log(discussionId);
    return httpClient
      .post(`/discussion/${discussionId}`)
      .then(response => {
        return new Discussion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async enrollInTourney(tourney: Tourney) {
    return httpClient
      .put(`/tourneys/${tourney.tourneyId}/enroll`)
      .then(response => {
        return new Tourney(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async cancelTourney(tourney: Tourney) {
    return httpClient
      .put(`/tourneys/${tourney.tourneyId}/cancel`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTourneyQuizAnswer(tourney: Tourney) {
    return httpClient
      .get(`/tourneys/${tourney.tourneyId}/quiz`)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async toggleTourneysPrivacy(privacy: String) : Promise<boolean> {
    return httpClient
        .put(`/student/tourneyprivacy/${privacy}`).then(response => response.data).catch(async error => {
          throw Error(await this.errorMessage(error));
        });
  }

  static async getTourneysPrivacy(privacy: String) : Promise<boolean> {
    return httpClient
        .get(`/student/tourneyprivacy`).then(response => response.data).catch(async error => {
          throw Error(await this.errorMessage(error));
        });
  }

  static async exportAll() {
    return httpClient
      .get('/admin/export', {
        responseType: 'blob'
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        let dateTime = new Date();
        link.setAttribute(
          'download',
          `export-${dateTime.toLocaleString()}.zip`
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      return 'Unauthorized access or Expired token';
    } else {
      console.log(error);
      return 'Unknown Error - Contact admin';
    }
  }
}
