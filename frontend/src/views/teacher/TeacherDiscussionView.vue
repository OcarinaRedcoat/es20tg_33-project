<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="discussions"
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
          <v-btn color="primary" dark @click="newCourse" data-cy="createButton"
            >New Course</v-btn
          >
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Discussion from '@/models/statement/Discussion';
@Component
export default class AllDiscussionView extends Vue {
  discussions: Discussion[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Discussion',
      value: 'name',
      align: 'left'
    },
    {
      text: 'Student Message',
      value: 'stMessage',
      align: 'center',
      width: '115px'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '7%',
      sortable: false
    }
  ];
}
</script>
