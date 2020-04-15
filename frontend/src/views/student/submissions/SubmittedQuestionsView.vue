<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="student_questions"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />

          <v-spacer />
          <v-btn color="primary" dark @click="submitQuestion"
            >Submit Question</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip v-if="item.status" :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>
    </v-data-table>
    <edit-student-question-dialog
      v-if="currentQuestion"
      v-model="editStudentQuestionDialog"
      :question="currentQuestion"
      v-on:submit-question="onSubmitQuestion"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import EditStudentQuestionDialog from '@/views/student/submissions/EditStudentQuestionDialog.vue';

@Component({
  components: {
    'edit-student-question-dialog': EditStudentQuestionDialog
  }
})
export default class SubmittedQuestionsView extends Vue {
  student_questions: StudentQuestion[] = [];
  currentQuestion: StudentQuestion | null = null;
  editStudentQuestionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    { text: 'Status', value: 'status', align: 'center', width: '10%' },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  @Watch('editStudentQuestionDialog')
  closeError() {
    if (!this.editStudentQuestionDialog) {
      this.currentQuestion = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.student_questions] = await Promise.all([
        RemoteServices.getStudentQuestions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, question: StudentQuestion) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'PENDING') return 'yellow';
    else return 'green';
  }

  submitQuestion() {
    this.currentQuestion = new StudentQuestion();
    this.editStudentQuestionDialog = true;
  }

  /*editQuestion(question: StudentQuestion) {
    this.currentQuestion = question;
    this.editQuestionDialog = true;
  }*/

  async onSubmitQuestion(question: StudentQuestion) {
    this.student_questions = this.student_questions.filter(
      q => q.id !== question.id
    );
    this.student_questions.unshift(question);
    this.editStudentQuestionDialog = false;
    this.currentQuestion = null;
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
