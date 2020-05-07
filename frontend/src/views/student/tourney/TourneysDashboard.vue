<template>
  <div class="container">
    <h2>Tourneys Dashboard</h2>
    <dashboard-list :headers="headers" :elements="tourneyStats" />
    <v-switch v-model="private" class="mx-2" @click="toggleTourneyPrivacy"></v-switch>
    <p>{{privacy}}</p>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator';
import RemoteServices from '../../../services/RemoteServices';
import DashboardList from '../../../components/DashboardList.vue';
import TourneyStats from '../../../models/tourney/TourneyStats';

@Component({
  components: { DashboardList }
})
export default class TourneysDashboard extends Vue {
  headers: object[] = [
    {
      name: 'Title',
      value: 'title'
    },
    {
      name: 'Completion Date',
      value: 'completionDate'
    },
    {
      name: 'Score',
      value: 'score'
    }
  ];
  tourneyStats: TourneyStats[] = [];

  private: boolean = true;

  async created() {
    this.tourneyStats = await RemoteServices.getTourneysDashboard();
    this.private = await RemoteServices.toggleTourneyPrivacy(this.privacy);
  }

  get privacy(){
    return !this.private?"private":"public";
  }

  async toggleTourneyPrivacy() {
    if (confirm("Are you sure you want to change this dashboard's privacy settings?")) {
      try {
        this.private = await RemoteServices.toggleTourneyPrivacy(this.privacy);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

}
</script>

<style lang="scss" scoped></style>
