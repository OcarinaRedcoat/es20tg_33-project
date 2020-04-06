import Option from '@/models/management/Option';
import User from '@/models/user/User';
import Course from '@/models/user/Course';

export default class StudentQuestion {
  id: number | undefined;
  title: string = '';
  status: string = 'PENDING';
  content: string = '';
  justification: string = '';
  submittingUser: User | undefined;
  course: Course | undefined;

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];

  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.content = jsonObj.content;
      this.justification = jsonObj.justification;
      this.submittingUser = jsonObj.submittingUser;
      this.course = jsonObj.course;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }
}
