import Message from '../statement/Message';

export default class Discussion {
  discussionId: number | undefined;
  studentMessage: Message | undefined;
  teacherMessage: Message | undefined;

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.discussionId = jsonObj.discussionId;
      this.studentMessage = jsonObj.studentMessage;
      this.teacherMessage = jsonObj.teacherMessage;
    }
  }
}
