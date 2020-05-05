<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Discussion Messages
        </span>
      </v-card-title>

      <v-data-table
        :headers="headers"
        :items="messagesList"
        :hide-default-footer="true"
        :mobile-breakpoint="0"
        multi-sort
        :items-per-page="5"
      >
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Discussion from '@/models/statement/Discussion';
import Message from '@/models/statement/Message';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditCourseDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;

  messagesList: Message[] = [];
  isCreateCourse: boolean = false;

  headers: object = [
    {
      text: 'Student/Teacher Name',
      value: 'name',
      align: 'left',
      width: '50%'
    },
    {
      text: 'Sentence',
      value: 'sentence',
      align: 'center',
      width: '50%'
    }
  ];

  async created() {
    try {
      console.log(this.discussion.id);
      this.messagesList = await RemoteServices.seeMessagesDiscussion(
        this.discussion.id
      );
      console.log(this.messagesList);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
