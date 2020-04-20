<template>
  <v-card class="table">
    <v-form ref="form" v-model="valid" lazy-validation data-cy="form">
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
            <v-btn
              color="primary"
              @click="teacherResponse"
              data-cy="teacherResponse"
              >Submit Teacher Response</v-btn
            >
          </v-card-title>
        </template>
      </v-data-table>
      <add-Response
        v-if="currentMessage"
        v-model="addResponse"
        :teacherResponse="currentMessage"
        v-on:new-teacher-response="teacherResponse"
        v-on:close-dialog="onCloseDialog"
      />
    </v-form>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/statement/Discussion';
import Message from '@/models/statement/Message';
import AddResponse from '@/views/teacher/AddResponse.vue';
import Course from '@/models/user/Course';
@Component({
  components: {
    'add-Response': AddResponse
  }
})
export default class AllDiscussionView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  currentMessage: Message | null = null;
  addResponse: boolean = false;
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

  teacherResponse() {
    this.addResponse = false;
    this.currentMessage = null;
  }

  onCloseDialog() {
    this.addResponse = false;
    this.currentMessage = null;
  }
}
</script>
