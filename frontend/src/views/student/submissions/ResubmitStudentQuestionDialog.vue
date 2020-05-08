<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="pt-9 mx-auto">
      <v-card-title>
        <span class="headline">
          Resubmit Question
        </span>
      </v-card-title>
      <v-container>
        <v-form ref="form" lazy-validation>
          <v-flex xs24 sm12 md12>
            <v-text-field
              class="py-5"
              v-model="resubmitStudentQuestion.justification"
              label="Rejection justification"
              data-cy="justification"
              readonly
              filled
              disabled
              outlined
            />
          </v-flex>
          <v-flex xs24 sm12 md12>
            <v-text-field
              class="py-5"
              v-model="resubmitStudentQuestion.title"
              label="Title"
              data-cy="Title"
            />
          </v-flex>
          <v-flex xs24 sm12 md12>
            <v-textarea
              class="py-5 mx-auto"
              v-model="resubmitStudentQuestion.content"
              label="Question Content"
              data-cy="QuestionContent"
            ></v-textarea>
          </v-flex>
          <v-flex
            class="py-5 mx-auto"
            xs24
            sm12
            md12
            v-for="index in resubmitStudentQuestion.options.length"
            :key="index"
          >
            <v-switch
              v-model="resubmitStudentQuestion.options[index - 1].correct"
              class="ma-4"
              label="Correct"
              data-cy="OptionCorrect"
            />
            <v-textarea
              v-model="resubmitStudentQuestion.options[index - 1].content"
              label="Option Content"
              data-cy="OptionContent"
            ></v-textarea>
          </v-flex>
          <v-card-actions>
            <v-spacer />
            <v-btn
              color="error"
              class="mr-4"
              @click="$emit('dialog', false)"
              data-cy="cancelButton"
              >Cancel</v-btn
            >
            <v-btn
              color="success"
              @click="resubmitQuestion"
              data-cy="resubmitButton"
              >Resubmit</v-btn
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
export default class ResubmitQuestionView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  resubmitStudentQuestion!: StudentQuestion;

  async created() {
    this.resubmitStudentQuestion = new StudentQuestion(this.question);
    this.resubmitStudentQuestion.submittingUser = await this.getSubmittingUser();
  }

  async resubmitQuestion() {
    if (
      this.resubmitStudentQuestion &&
      (!this.resubmitStudentQuestion.title ||
        !this.resubmitStudentQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    } else {
      try {
        const result = await RemoteServices.resubmitQuestion(
          this.resubmitStudentQuestion.id,
          this.resubmitStudentQuestion
        );
        this.$emit('submit-question', result);
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
