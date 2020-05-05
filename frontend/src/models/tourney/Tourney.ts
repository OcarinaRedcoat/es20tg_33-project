import User from '../user/User';
import Course from '../user/Course';
import Topic from '../management/Topic';
import StatementQuiz from "@/models/statement/StatementQuiz";

export default class Tourney {
  tourneyId: number | undefined;
  tourneyStatus: string = 'OPEN';
  tourneyTitle: string | undefined;
  tourneyNumberOfQuestions: number | undefined;
  tourneyAvailableDate: string = '';
  tourneyConclusionDate: string | undefined;
  tourneyTopics: Topic[] | undefined;
  tourneyCreator: User | undefined;
  tourneyCourseExecution: Course | undefined;
  quizStatement: StatementQuiz | undefined;

  constructor(jsonObj?: Tourney) {
    if (jsonObj) {
      this.tourneyId = jsonObj.tourneyId;
      this.tourneyStatus = jsonObj.tourneyStatus;
      this.tourneyTitle = jsonObj.tourneyTitle;
      this.tourneyNumberOfQuestions = jsonObj.tourneyNumberOfQuestions;
      this.tourneyAvailableDate = jsonObj.tourneyAvailableDate;
      this.tourneyConclusionDate = jsonObj.tourneyConclusionDate;
      this.tourneyTopics = jsonObj.tourneyTopics;
      this.tourneyCreator = jsonObj.tourneyCreator;
      this.tourneyCourseExecution = jsonObj.tourneyCourseExecution;
      this.quizStatement = jsonObj.quizStatement;
    }
  }
}
