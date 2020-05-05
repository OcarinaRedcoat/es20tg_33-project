import User from '@/models/user/User';
import Message from '@/models/statement/Message';

export default class Discussion {
  quizAnswerId: number | undefined;
  id: number | undefined;
  courseId: number | undefined;
  creatorStudent: User | undefined;
  discussionListMessages: Message[] = [];
  title: string | undefined;
  status: string | undefined;

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.quizAnswerId = jsonObj.quizAnswerId;
      this.courseId = jsonObj.courseId;
      this.creatorStudent = jsonObj.creatorStudent;
      this.discussionListMessages = jsonObj.discussionListMessages;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
    }
  }
}
