<template>
    <v-card class="table">
        <v-data-table
                :headers="headers"
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

        </v-data-table>
    </v-card>
</template>

<script lang="ts">
  import { Component, Vue, Watch } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Question from '@/models/management/Question';
  import StudentQuestion from '@/models/submissions/StudentQuestion';
  import Image from '@/models/management/Image';
  import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
  import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
  import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';

  @Component({
    components: {
      'show-question-dialog': ShowQuestionDialog,
      'edit-question-dialog': EditQuestionDialog,
      'edit-question-topics': EditQuestionTopics
    }
  })
  export default class StudentQuestionsView extends Vue {
    questions: StudentQuestion[] = [];
    editQuestionDialog: boolean = false;
    search: string = '';

    headers: object = [
      {
        text: 'Id',
        value: 'id',
        align: 'center',
      },
      { text: 'Title', value: 'title', align: 'center' },
      { text: 'Question', value: 'content', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' },
      {
        text: 'Creation Date',
        value: 'creationDate',
        align: 'center'
      },
      {
        text: 'Image',
        value: 'image',
        align: 'center',
        sortable: false
      },
      {
        text: 'Actions',
        value: 'action',
        align: 'center',
        sortable: false
      }
    ];

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
