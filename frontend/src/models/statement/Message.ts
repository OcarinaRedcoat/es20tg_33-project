import User from '../user/User';
import Discussion from '@/models/statement/Discussion';

export default class Message {
  id: number | undefined;
  message: string | undefined;
  messageDate: string | undefined;
  user: User | undefined;
  discussion: Discussion | undefined;

  constructor(jsonObj?: Message) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.message = jsonObj.message;
      this.messageDate = jsonObj.messageDate;
      this.user = jsonObj.user;
    }
  }
}