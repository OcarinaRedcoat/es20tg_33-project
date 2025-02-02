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
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="createNewMessage(item)"
                >fas fa-envelope-square</v-icon
              >
            </template>
            <span>Create new Message</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="seeMessages(item)"
                >far fa-eye</v-icon
              >
            </template>
            <span>See Messages</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                data-cy="changePublicStatus"
                @click="changePublicStatus(item)"
                >cached</v-icon
              >
            </template>
            <span>Change Public Status</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-form>
    <add-response
      v-if="currentDiscussion"
      v-model="editDiscussionDialog"
      :discussion="currentDiscussion"
      v-on:close-dialog="onCloseDialog"
    />
    <show-messages-dialog
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
import AddResponse from '@/views/teacher/AddResponse.vue';
import ShowMessagesDialog from '@/views/teacher/ShowMessagesDialog.vue';
import Course from '@/models/user/Course';
@Component({
  components: {
    'show-messages-dialog': ShowMessagesDialog,
    'add-response': AddResponse
  }
})
export default class TeacherDiscussionView extends Vue {
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
      width: '22.5%'
    },
    {
      text: 'Student',
      value: 'creatorStudent.name',
      align: 'center',
      width: '22.5%'
    },
    {
      text: 'Solved Status',
      value: 'solvedStatus',
      align: 'center',
      width: '22.5%'
    },
    {
      text: 'Discussion Status',
      value: 'status',
      align: 'center',
      width: '22.5%'
    },
    {
      text: 'Create/See Messages and Status',
      value: 'action',
      align: 'center',
      width: '10%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.discussions = await RemoteServices.getDiscussions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  createNewMessage(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.editDiscussionDialog = true;
  }

  seeMessages(discussion: Discussion) {
    this.currentDiscussion = new Discussion(discussion);
    this.seeMessagesDialog = true;
  }

  async changePublicStatus(discussionChange: Discussion) {
    if (
      confirm('Are you sure you want to change the status of this discussion?')
    ) {
      try {
        const result = await RemoteServices.makeDiscussionPublic(
          discussionChange.id
        );
        this.discussions.forEach(function(value) {
          if (value.id == result.id) {
            value.status = result.status;
          }
        });
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  onCloseDialog() {
    this.seeMessagesDialog = false;
    this.editDiscussionDialog = false;
    this.currentDiscussion = null;
  }
}
</script>
