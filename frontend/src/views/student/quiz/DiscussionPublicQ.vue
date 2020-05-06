<template>
  <v-card class="table">
    <v-form ref="form">
      <v-card-title>Responses</v-card-title>
      <v-data-table
        :headers="headers"
        :items="discussions"
        :search="search"
        disable-pagination
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        multi-sort
        :items-per-page="5"
        :footer-props="{ itemsPerPageOptions: [5, 10, 15, 50, 100] }"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
              data-cy="searchDiscussion"
            />
            <v-spacer />
            <v-spacer />
          </v-card-title>
        </template>
        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="seeMessages(item)"
                >far fa-eye</v-icon
              >
            </template>
            <span>See Messages</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-form>
    <edit-discussion-dialog
      v-if="currentDiscussion"
      v-model="editDiscussionDialog"
      :discussion="currentDiscussion"
      v-on:close-dialog="onCloseDialog"
    />
    <see-discussion-messages-dialog
      v-if="currentDiscussion"
      v-model="seeMessagesDialog"
      :discussion="currentDiscussion"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/statement/Discussion';
import EditDiscussionDialog from '@/views/student/quiz/EditDiscussionDialog.vue';
import SeeDiscussionMessagesDialog from '@/views/student/quiz/SeeDiscussionMessagesDialog.vue';
import StatementManager from '@/models/statement/StatementManager';
@Component({
  components: {
    'see-discussion-messages-dialog': SeeDiscussionMessagesDialog,
    'edit-discussion-dialog': EditDiscussionDialog
  }
})
export default class DiscussionPublicQ extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  discussions: Discussion[] = [];
  search: string = '';
  currentDiscussion: Discussion | null = null;
  editDiscussionDialog: boolean = false;
  seeMessagesDialog: boolean = false;
  headers: object = [
    {
      text: 'Discussion',
      value: 'title',
      align: 'left',
      width: '30%'
    },
    {
      text: 'Student',
      value: 'creatorStudent.name',
      align: 'center',
      width: '30%'
    },
    {
      text: 'Solved Status',
      value: 'solvedStatus',
      align: 'center',
      width: '30%'
    },
    {
      text: 'Create/See Messages',
      value: 'action',
      align: 'center',
      width: '10%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.getPublicDiscussionsQuizz(
        this.statementManager.statementQuiz?.quizAnswerId
      );
      console.log(this.discussions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  seeMessages(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.seeMessagesDialog = true;
  }

  onCloseDialog() {
    this.seeMessagesDialog = false;
    this.editDiscussionDialog = false;
    this.currentDiscussion = null;
  }
}
</script>
