<template>
  <v-card>
    <h1>Submit Message</h1>
    <v-container>
      <v-form ref="form" v-model="valid" lazy-validation data-cy="form">
        <v-row justify="space-between">
          <v-col cols="12" md="100">
            <v-text-field
              v-model="submitMessage.message"
              :rules="[v => !!v || 'Must have a Message']"
              label="Message"
              data-cy="message"
              required
            ></v-text-field>
          </v-col>
        </v-row>
        <v-btn color="error" class="mr-4" @click="reset">Clear</v-btn>

        <v-btn
          :disabled="!valid"
          color="success"
          class="mr-4"
          data-cy="submitMessage"
          @click="saveMessage"
          >Submit</v-btn
        >
      </v-form>
    </v-container>
  </v-card>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Message from '@/models/statement/Message';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class DiscussionAnswerForm extends Vue {
  submitMessage!: Message;
  valid: boolean = false;
  questionAnswerId!: number;

  async created() {
    this.submitMessage = new Message();
    this.valid = false;
    this.questionAnswerId = await this.getQuestionAnswerId();
  }

  async getQuestionAnswerId() {
    const questionAnswerId = this.$store.getters.getStatementAnswer
      .questionAnswerId;
    if (questionAnswerId == null) {
      await this.$store.dispatch('error', 'Can\'t find questionAnswerId');
    }
    return questionAnswerId;
  }

  async saveMessage() {
    if (!this.submitMessage.message) {
      await this.$store.dispatch('error', 'Please fill the form');
      return;
    } else {
      try {
        const result = await RemoteServices.submitStudentAnswer(
          this.questionAnswerId,
          this.submitMessage
        );
        this.$emit('new-message', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  reset() {
    (this.$refs.form as Vue & { reset: () => boolean }).reset();
    this.submitMessage = new Message();
  }
}
</script>
