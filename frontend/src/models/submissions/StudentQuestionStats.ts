export default class StudentQuestion {
  approved: number | undefined;
  submitted: number | undefined;
  pending: number | undefined;

  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.approved = jsonObj.approved;
      this.submitted = jsonObj.submitted;
      this.pending = jsonObj.pending;
    }
  }
}