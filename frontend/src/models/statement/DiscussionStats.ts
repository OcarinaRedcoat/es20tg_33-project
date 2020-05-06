export default class DiscussionStats {
  username: string | undefined;
  name: string | undefined;
  numberOfDiscussions: number | undefined;
  numberOfSolvedDiscussions: number | undefined;
  percentage: number | undefined;

  constructor(jsonObj?: DiscussionStats) {
    if (jsonObj) {
      this.username = jsonObj.username;
      this.name = jsonObj.name;
      this.numberOfDiscussions = jsonObj.numberOfDiscussions;
      this.numberOfSolvedDiscussions = jsonObj.numberOfSolvedDiscussions;
      this.percentage = jsonObj.percentage;
    }
  }
}
