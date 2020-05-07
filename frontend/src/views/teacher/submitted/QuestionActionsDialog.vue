<template>
  <v-dialog
    v-model="dialog"
    @input="$emit('review-question', false)"
    @keydown.esc="$emit('review-question', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ question.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-question :question="question" />
      </v-card-text>
      <v-container>
        <v-form ref="form" lazy-validation>
          <v-flex xs24 sm12 md12>
            <v-text-field
              class="py-5"
              v-model="editQuestion.justification"
              label="Justification"
              data-cy="justification"
              clearable
            />
          </v-flex>
        </v-form>
      </v-container>
      <v-card-actions>
        <v-spacer />
        <v-spacer />
        <v-spacer />
        <v-btn
          dark
          color="green darken-1"
          @click="approveQuestion"
          data-cy="approveButton"
          >approve</v-btn
        >
        <v-btn
          dark
          color="red darken-1"
          @click="rejectQuestion"
          data-cy="rejectButton"
          >reject</v-btn
        >
        <v-btn
          dark
          color="blue darken-1"
          @click="$emit('review-question')"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import ShowStudentQuestion from '@/views/teacher/submitted/ShowStudentQuestion.vue';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'show-question': ShowStudentQuestion
  }
})
export default class ReviewQuestionDialog extends Vue {
  @Model('review-question', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  editQuestion!: StudentQuestion;

  created() {
    this.editQuestion = new StudentQuestion(this.question);
  }

  async approveQuestion() {
    if (
      this.editQuestion.justification == null ||
      this.editQuestion.justification == ''
    ) {
      try {
        const result = await RemoteServices.approveQuestion(
          this.editQuestion.id,
          'none'
        );
        this.$emit('review-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else {
      try {
        const result = await RemoteServices.approveQuestion(
          this.editQuestion.id,
          this.editQuestion.justification
        );
        this.$emit('review-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async rejectQuestion() {
    if (
      this.editQuestion.justification == null ||
      this.editQuestion.justification == ''
    ) {
      await this.$store.dispatch(
        'error',
        'Error: Rejection must have a justification'
      );
      return;
    } else {
      try {
        const result = await RemoteServices.rejectQuestion(
          this.editQuestion.id,
          this.editQuestion.justification
        );
        this.$emit('review-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
