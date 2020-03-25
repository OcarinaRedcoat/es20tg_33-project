import User from '../user/User';
import Course from '../user/Course';

export default class Tourney {
  tourneyId: number | undefined;
  tourneyStatus: string = 'OPEN';
  tourneyName: string | undefined;
  tourneyNumberOfQuestions: number | undefined;
  tourneyAvailableDate: string = '';
  tourneyConclusionDate: string | undefined;
  tourneyCreator: User | undefined;
  tourneyCourseExecution: Course | undefined;

  constructor(jsonObj?: Tourney) {
    if (jsonObj) {
      this.tourneyId = jsonObj.tourneyId;
      this.tourneyStatus = jsonObj.tourneyStatus;
      this.tourneyName = jsonObj.tourneyName;
      this.tourneyNumberOfQuestions = jsonObj.tourneyNumberOfQuestions;
      this.tourneyAvailableDate = jsonObj.tourneyAvailableDate;
      this.tourneyConclusionDate = jsonObj.tourneyConclusionDate;
      this.tourneyCreator = jsonObj.tourneyCreator;
      this.tourneyCourseExecution = jsonObj.tourneyCourseExecution;
    }
  }
}
