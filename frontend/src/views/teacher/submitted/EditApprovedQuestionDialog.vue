<template>
  <v-dialog
    :value="dialog"
    @input="$emit('edit-approved-question', false)"
    @keydown.esc="$emit('edit-approved-question', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="pt-9 mx-auto">
      <v-card-title>
        <span class="headline">
          Edit Question
        </span>
      </v-card-title>
      <v-container>
        <v-form ref="form" lazy-validation>
          <v-flex xs24 sm12 md12>
            <v-text-field
              class="py-5"
              v-model="editApprovedQuestion.title"
              label="Title"
              data-cy="editApprovedQuestionTitle"
            />
          </v-flex>
          <v-flex xs24 sm12 md12>
            <v-textarea
              class="py-5 mx-auto"
              v-model="editApprovedQuestion.content"
              label="Question Content"
              data-cy="editApprovedQuestionContent"
            ></v-textarea>
          </v-flex>
          <v-flex
            class="py-5 mx-auto"
            xs24
            sm12
            md12
            v-for="index in editApprovedQuestion.options.length"
            :key="index"
          >
            <v-switch
              v-model="editApprovedQuestion.options[index - 1].correct"
              class="ma-4"
              label="Correct"
              data-cy="editApprovedQuestionOptionCorrect"
            />
            <v-textarea
              v-model="editApprovedQuestion.options[index - 1].content"
              data-cy="editApprovedQuestionOptionContent"
              :label="`Option ${index}`"
            ></v-textarea>
          </v-flex>
          <v-card-actions>
            <v-spacer />
            <v-btn
              color="error"
              class="mr-4"
              @click="$emit('edit-approved-question', false)"
              >Cancel</v-btn
            >
            <v-btn
              color="success"
              @click="saveQuestion"
              data-cy="saveApprovedQuestionChangesButton"
              >Save</v-btn
            >
          </v-card-actions>
        </v-form>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class SubmitQuestionView extends Vue {
  @Model('edit-approved-question', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  editApprovedQuestion!: StudentQuestion;

  async created() {
    this.editApprovedQuestion = new StudentQuestion(this.question);
    this.editApprovedQuestion.submittingUser = await this.getSubmittingUser();
  }

  async saveQuestion() {
    if (
      this.editApprovedQuestion &&
      (!this.editApprovedQuestion.title || !this.editApprovedQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    } else {
      try {
        const result = await RemoteServices.saveApprovedQuestionChanges(
          this.editApprovedQuestion
        );
        this.$emit('edit-approved-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async getSubmittingUser() {
    const user = this.$store.getters.getUser.getUsername();
    if (user == null) {
      await this.$store.dispatch('error', 'Can\'t find user');
    }
    return user;
  }
}
</script>
