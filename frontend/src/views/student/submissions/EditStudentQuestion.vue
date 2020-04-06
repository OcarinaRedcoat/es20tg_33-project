<template>
  <v-card>
    <v-container>
      <v-form ref="form" lazy-validation>
        <v-flex xs24 sm12 md12>
          <v-text-field
            v-model="editStudentQuestion.title"
            label="Title"
            clearable
          />
        </v-flex>
        <v-flex xs24 sm12 md12>
          <v-textarea
            clearable
            v-model="editStudentQuestion.content"
            label="Question Content"
          ></v-textarea>
        </v-flex>
        <v-flex
          xs24
          sm12
          md12
          v-for="index in editStudentQuestion.options.length"
          :key="index"
        >
          <v-switch
            clearable
            v-model="editStudentQuestion.options[index - 1].correct"
            class="ma-4"
            label="Correct"
          />
          <v-textarea
            v-model="editStudentQuestion.options[index - 1].content"
            label="Option Content"
          ></v-textarea>
        </v-flex>
        <v-card-actions>
          <v-spacer />
          <v-btn color="error" class="mr-4" @click="reset">Clear</v-btn>
          <v-btn color="success" @click="submitQuestion">Submit</v-btn>
        </v-card-actions>
      </v-form>
    </v-container>
  </v-card>
</template>

<script lang="ts">
  import {Component, Prop, Vue} from 'vue-property-decorator';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class SubmitQuestionView extends Vue {
  @Prop({ type: StudentQuestion, required: true }) readonly question!: StudentQuestion;

  editStudentQuestion!: StudentQuestion;

  async created() {
    this.editStudentQuestion = new StudentQuestion(this.question);
    this.editStudentQuestion.course = await this.getCourse();
    this.editStudentQuestion.submittingUser = await this.getSubmittingUser();
  }

  async submitQuestion() {
    if (
      this.editStudentQuestion &&
      (!this.editStudentQuestion.title || !this.editStudentQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    } else {
      try {
        const result = await RemoteServices.submitQuestion(
          this.editStudentQuestion
        );
        this.$emit('submit-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async getCourse() {
    const course = this.$store.getters.getCurrentCourse;
    if (course == null) {
      await this.$store.dispatch('error', 'Can\'t find course');
    }
    return course;
  }

  async getSubmittingUser() {
    const user = this.$store.getters.getUser;
    if (user == null) {
      await this.$store.dispatch('error', 'Can\'t find user');
    }
    return user;
  }

  reset() {
    this.$refs.form.reset();
  }
}
</script>
