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
          New Message
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editMessage">
        <v-container grid-list-md fluid>
          <v-flex xs24 sm12 md8>
            <v-text-field
              v-model="editMessage.sentence"
              label="Sentence"
              data-cy="sentence"
            />
          </v-flex>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveMessage" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import Discussion from '@/models/statement/Discussion';
import Message from '@/models/statement/Message';

@Component
export default class EditCourseDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Discussion, required: true }) readonly discussion!: Discussion;

  editMessage!: Message;
  editDiscussion!: Discussion;

  created() {
    console.log(this.discussion.id);
    this.editDiscussion = new Discussion(this.discussion);
    this.editMessage = new Message();
  }

  async saveMessage() {

    if (!this.editMessage.sentence) {
      await this.$store.dispatch('error', 'Message must have a sentence!');
      return;
    }

    if (this.editMessage) {
      try {
        const result = await RemoteServices.submitMessage(
          this.editDiscussion.id,
          this.editMessage
        );
        this.$emit('new-course', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
