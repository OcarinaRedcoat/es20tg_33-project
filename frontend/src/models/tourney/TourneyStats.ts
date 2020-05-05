export default class Tourney {
	id: number | undefined;
  title: string | undefined;
  numberOfQuestions: number | undefined;
  completionDate: string | undefined;
  score: string | undefined;

  constructor(jsonObj?: Tourney) {
    if (jsonObj) {
			this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.completionDate = jsonObj.completionDate;
      this.score = jsonObj.score;
    }
  }
}
