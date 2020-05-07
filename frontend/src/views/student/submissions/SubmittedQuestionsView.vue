<template>
  <div class="container">
    <br />
    <h2>Submitted Questions</h2>
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
            <v-btn
              color="primary"
              dark
              @click="submitQuestion"
              data-cy="createSubmissionButton"
              >Submit Question</v-btn
            >
          </v-card-title>
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip v-if="item.status" :color="getStatusColor(item.status)" small>
            <span>{{ item.status }}</span>
          </v-chip>
        </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                medium
                class="mr-2"
                v-on="on"
                @click="showStudentQuestionDialog(item)"
                >visibility</v-icon
              >
            </template>
            <span>Show Question</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.status !== ('APPROVED' || 'AVAILABLE')">
            <template v-slot:activator="{ on }">
              <v-icon
                medium
                class="mr-2"
                v-on="on"
                @click="deleteSubmittedQuestion(item)"
                color="red"
                data-cy="deleteSubmittedQuestion"
                >delete</v-icon
              >
            </template>
            <span>Delete Question</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.status === 'REJECTED'">
            <template v-slot:activator="{ on }">
              <v-icon
                medium
                class="mr-2"
                v-on="on"
                @click="resubmitQuestion(item)"
                data-cy="ResubmitQuestion"
                >fas fa-edit</v-icon
              >
            </template>
            <span>Resubmit Question</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <edit-student-question-dialog
        v-if="currentQuestion"
        v-model="editStudentQuestionDialog"
        :question="currentQuestion"
        v-on:submit-question="onSubmitQuestion"
      />
      <show-student-question-dialog
        v-if="currentQuestion"
        v-model="studentQuestionDialog"
        :question="currentQuestion"
        v-on:close-show-question-dialog="onCloseShowQuestionDialog"
      />
      <resubmit-student-question-dialog
        v-if="currentQuestion"
        v-model="resubmitStudentQuestionDialog"
        :question="currentQuestion"
        v-on:submit-question="onResubmitQuestion"
      />
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import EditStudentQuestionDialog from '@/views/student/submissions/EditStudentQuestionDialog.vue';
import ResubmitStudentQuestionDialog from '@/views/student/submissions/ResubmitStudentQuestionDialog.vue';
import Question from '@/models/management/Question';
import ShowStudentQuestionDialog from '@/views/student/submissions/ShowStudentQuestionDialog.vue';

@Component({
  components: {
    'show-student-question-dialog': ShowStudentQuestionDialog,
    'edit-student-question-dialog': EditStudentQuestionDialog,
    'resubmit-student-question-dialog': ResubmitStudentQuestionDialog
  }
})
export default class SubmittedQuestionsView extends Vue {
  student_questions: StudentQuestion[] = [];
  currentQuestion: StudentQuestion | null = null;
  editStudentQuestionDialog: boolean = false;
  studentQuestionDialog: boolean = false;
  resubmitStudentQuestionDialog: boolean = false;
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

  resubmitQuestion(question: StudentQuestion) {
    this.currentQuestion = question;
    this.resubmitStudentQuestionDialog = true;
  }

  async onSubmitQuestion(question: StudentQuestion) {
    this.student_questions = this.student_questions.filter(
      q => q.id !== question.id
    );
    this.student_questions.unshift(question);
    this.editStudentQuestionDialog = false;
    this.currentQuestion = null;
  }

  showStudentQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.studentQuestionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.studentQuestionDialog = false;
    this.currentQuestion = null;
  }

  async onResubmitQuestion(question: StudentQuestion) {
    this.student_questions = this.student_questions.filter(
      q => q.id !== question.id
    );
    this.student_questions.unshift(question);
    this.resubmitStudentQuestionDialog = false;
  }

  async deleteSubmittedQuestion(toDeleteStudentQuestion: StudentQuestion) {
    if (
      toDeleteStudentQuestion.id &&
      confirm('Are you sure you want cancel this question submission?')
    ) {
      try {
        await RemoteServices.deleteStudentQuestion(toDeleteStudentQuestion.id);
        this.student_questions = this.student_questions.filter(
          question => question.id != toDeleteStudentQuestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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
