import User from '../user/User';
import Discussion from '@/models/statement/Discussion';

export default class Message {
  id: number | undefined;
  discussionId: number | undefined;
  messageDate: string | undefined;
  userName: string | undefined;
  name: string | undefined;
  sentence: string | undefined;

  constructor(jsonObj?: Message) {
    if (jsonObj) {
      console.log(jsonObj);
      this.id = jsonObj.id;
      this.discussionId = jsonObj.discussionId;
      this.messageDate = jsonObj.messageDate;
      this.userName = jsonObj.userName;
      this.name = jsonObj.name;
      this.sentence = jsonObj.sentence;
    }
  }
}
