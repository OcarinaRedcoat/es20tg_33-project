<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :custom-filter="customFilter"
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
                <v-chip :color="getStatusColor(item)" small>
                    <span>{{ item.status }}</span>
                </v-chip>
            </template>
        </v-data-table>
    </v-card>
</template>

<script lang="ts">
  import { Component, Vue, Watch } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Question from '@/models/management/Question';
  import StudentQuestion from '@/models/submissions/StudentQuestion';
  import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';

  @Component({
    components: {
      'edit-question-dialog': EditQuestionDialog,
    }
  })
  export default class StudentQuestionsView extends Vue {
    questions: StudentQuestion[] = [];
    currentQuestion: StudentQuestion | null = null;
    editStudentQuestionDialog: boolean = false;
    statusList = ['PENDING', 'APPROVED', 'REJECTED'];
    search: string = '';

    headers: object = [
      { text: 'Id', value: 'id', align: 'center', },
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

