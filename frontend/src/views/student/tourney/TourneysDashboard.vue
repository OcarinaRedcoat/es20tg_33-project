<template>
  <div class="container">
    <h2>Tourneys Dashboard</h2>
    <dashboard-list :headers="headers" :elements="tourneyStats" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
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

  async created() {
    this.tourneyStats = await RemoteServices.getTourneysDashboard();
  }

}
</script>

<style lang="scss" scoped></style>
