<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
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
        </v-card-title>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
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
              @click="showQuestionDialog(item)"
              data-cy="reviewQuestionButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Question and see actions</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.status === 'APPROVED'">
          <template v-slot:activator="{ on }">
            <v-icon
              medium
              class="mr-2"
              v-on="on"
              @click="makeQuestionAvailable(item)"
              data-cy="makeQuestionAvailableButton"
              >done</v-icon
            >
          </template>
          <span>Make Question Available</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.status === 'APPROVED'">
          <template v-slot:activator="{ on }">
            <v-icon
              medium
              class="mr-2"
              v-on="on"
              @click="editApprovedQuestion(item)"
              data-cy="editApprovedQuestionButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Approved Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <question-actions-dialog
      v-if="currentQuestion"
      :dialog="questionDialog"
      :question="currentQuestion"
      v-on:review-question="onReviewQuestion"
    />
    <edit-approved-question
      v-if="currentQuestion"
      :dialog="editStudentQuestionDialog"
      :question="currentQuestion"
      v-on:edit-approved-question="onSaveQuestion"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/submissions/StudentQuestion';
import Question from '@/models/management/Question';
import QuestionActionsDialog from '@/views/teacher/submitted/QuestionActionsDialog.vue';
import EditApprovedQuestionDialog from '@/views/teacher/submitted/EditApprovedQuestionDialog.vue';

@Component({
  components: {
    'question-actions-dialog': QuestionActionsDialog,
    'edit-approved-question': EditApprovedQuestionDialog
  }
})
export default class StudentQuestionsView extends Vue {
  questions: StudentQuestion[] = [];
  currentQuestion: StudentQuestion | null = null;
  editStudentQuestionDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Id', value: 'id', align: 'center' },
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    { text: 'Status', value: 'status', align: 'center' },
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
      [this.questions] = await Promise.all([
        RemoteServices.getPendingQuestions()
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

  showQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  editApprovedQuestion(question: StudentQuestion) {
    this.currentQuestion = question;
    this.editStudentQuestionDialog = true;
  }

  async onReviewQuestion(question: StudentQuestion) {
    this.questions = this.questions.filter(q => q.id !== question.id);
    this.questions.unshift(question);
    this.questionDialog = false;
    this.currentQuestion = null;
  }

  async onSaveQuestion(question: StudentQuestion) {
    this.questions = this.questions.filter(q => q.id !== question.id);
    this.questions.unshift(question);
    this.editStudentQuestionDialog = false;
    this.currentQuestion = null;
  }

  async makeQuestionAvailable(studentQuestion: StudentQuestion) {
    if (
      studentQuestion.id &&
      confirm('Are you sure you want to make this question available?')
    ) {
      try {
        await RemoteServices.makeQuestionAvailable(studentQuestion.id);
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
