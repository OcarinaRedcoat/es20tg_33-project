<template>
  <v-card class="table">
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
          />
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
              @click="deleteQuestion(item)"
              color="red"
              >delete</v-icon
            >
          </template>
          <span>Delete Student Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-teacher-response
      v-if="currentMessage"
      v-model="editCourseDialog"
      :teacherResponse="currentMessage"
      v-on:new-teacher-response="onCreateTeacherResponse"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/statement/Discussion';
import Message from '@/models/statement/Message';
import Course from '@/models/user/Course';
@Component
export default class AllDiscussionView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  currentMessage: Message | null = null;
  editCourseDialog: boolean = false;
  headers: object = [
    {
      text: 'Discussion',
      value: 'discussionId',
      align: 'left',
      width: '45%'
    },
    {
      text: 'Student Message',
      value: 'studentMessage.message',
      align: 'center',
      width: '45%'
    },
    {
      text: 'Actions',
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
      console.log(this.discussions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async onCreateTeacherResponse(message: Message) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.teacherMessageSub(message);
      console.log(this.discussions);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  onCloseDialog() {
    this.editCourseDialog = false;
    this.currentMessage = null;
  }
}
</script>
