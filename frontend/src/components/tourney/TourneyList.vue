<template>
  <v-card>
    <v-container>
      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="tourneys"
        :search="search"
        :mobile-breakpoint="0"
        :items-per-page="50"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        data-cy="tourneysList"
      >
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              single-line
              hide-details
            />
            <v-spacer />
            <v-btn color="primary" dark to="tourneys/create">New Tourney</v-btn>
          </v-card-title>
        </template>
        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="enrollInTourney(item)" data-cy="enrollInTourney">add</v-icon>
            </template>
            <span>Enroll in Tourney</span>
          </v-tooltip>
          <v-tooltip bottom v-if="isCreator(item)">
            <template v-slot:activator="{ on }">
              <v-icon small class="mr-2" v-on="on" @click="cancelTourney(item)" color="red" data-cy="cancelTourney">delete</v-icon>
            </template>
            <span>Cancel</span>
          </v-tooltip>
        </template>
      </v-data-table>
      <!-- End Table -->
    </v-container>
  </v-card>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Tourney from '../../models/tourney/Tourney';
import RemoteServices from '../../services/RemoteServices';
import User from "@/models/user/User";

@Component
export default class TourneyList extends Vue {
  tourneys: Tourney[] = [];
  username: string = '';
  search: string = '';
  headers: object = [
    { text: 'Tourney', value: 'tourneyTitle', align: 'left' },
    {
      text: 'Number of Questions',
      value: 'tourneyNumberOfQuestions',
      align: 'center',
      width: '115px'
    },
    {
      text: 'Available Date',
      value: 'tourneyAvailableDate',
      align: 'center',
      width: '10%',
      sortable: false
    },
    {
      text: 'Conclusion Date',
      value: 'tourneyConclusionDate',
      align: 'center',
      width: '10%',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '7%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
			this.tourneys = await RemoteServices.getTourneys();
			this.username = await this.$store.getters.getUser.username;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string) {
    return (
      search != null &&
      typeof value === 'string' &&
      value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
    );
  }

  isCreator(tourney: Tourney) {
    return tourney?.tourneyCreator?.username === this.username;
  }

  async enrollInTourney(tourney: Tourney) {
    if (confirm('Are you sure you want to enroll in this tourney?')) {
      try {
        await RemoteServices.enrollInTourney(tourney);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async cancelTourney(toDeleteTourney: Tourney) {
    if (confirm('Are you sure you want to cancel this tourney?')) {
      try {
        await RemoteServices.cancelTourney(toDeleteTourney);
        this.tourneys = this.tourneys.filter(tourney => tourney.tourneyId !== toDeleteTourney.tourneyId)
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
