<template>
  <div class="container">
    <h2>Tourneys Dashboard</h2>
    <div class="dashboard-card">
      <v-switch v-model="private" class="mx-2"></v-switch>
      <span>{{privacy}}</span>
    </div>
    <dashboard-list :headers="headers" :elements="tourneyStats" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
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
  first: boolean = false;

  async created() {
    this.tourneyStats = await RemoteServices.getTourneysDashboard();
    this.private = await RemoteServices.getTourneysPrivacy(this.privacy);
    this.first = !this.private; // avoid confirm dialog when the value at the DB is false and therefore changes this.private.
  }

  get privacy() {
    return this.private ? 'private' : 'public';
  }

  @Watch('private', { deep: true })
  async toggleTourneyPrivacy() {
    if (this.first) {
      this.first = false;
      return;
    }
    const conf = confirm(
      'Are you sure you want to change this dashboard\'s privacy settings?'
    );
    if (conf) {
      try {
        this.private = await RemoteServices.toggleTourneysPrivacy(this.privacy);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-card {
  display: flex;
  background: #fff;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}
.dashboard-card span {
  text-transform: capitalize;
  display: flex;
  flex-direction: column;
  align-self: center;
  font-size: 1.2rem;
}
</style>
