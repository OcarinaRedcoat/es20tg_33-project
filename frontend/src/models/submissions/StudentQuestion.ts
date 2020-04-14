import Option from '@/models/management/Option';

export default class StudentQuestion {
  id: number | undefined;
  title: string = '';
  status: string = 'PENDING';
  content: string = '';
  justification: string = '';
  submittingUser: string = '';

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];

  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.content = jsonObj.content;
      this.justification = jsonObj.justification;
      this.submittingUser = jsonObj.submittingUser;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }
}
