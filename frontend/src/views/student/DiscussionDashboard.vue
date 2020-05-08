<template>
  <div class="container">
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.numberOfDiscussions" />
        </div>
        <div class="project-name">
          <p>Total Discussion Submited</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.numberOfSolvedDiscussions" />
        </div>
        <div class="project-name">
          <p>Total Discussion Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number :number="stats.percentage">%</animated-number>
        </div>
        <div class="project-name">
          <p>Percentage Discussion Solved</p>
        </div>
      </div>
    </div>
    <v-card class="table">
      <v-form ref="form">
        <v-card-title>Public Dashboards</v-card-title>
        <v-data-table
          :headers="headers"
          :items="dashboards"
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
                dark
                @click="changePrivacy"
                data-cy="createButton"
                >Change Dashboard Privacy</v-btn
              >
            </v-card-title>
            <v-spacer />
          </template>
        </v-data-table>
      </v-form>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import DiscussionStats from '@/models/statement/DiscussionStats';

@Component({
  components: { AnimatedNumber }
})
export default class DiscussionDashboard extends Vue {
  stats: DiscussionStats | null = null;
  dashboards: DiscussionStats[] = [];
  search: string = '';

  headers: object = [
    {
      text: 'Student',
      value: 'name',
      align: 'left',
      width: '25%'
    },
    {
      text: 'Number Of Discussions Created',
      value: 'numberOfDiscussions',
      align: 'center',
      width: '25%'
    },
    {
      text: 'Number Of Discussions Solved',
      value: 'numberOfSolvedDiscussions',
      align: 'center',
      width: '25%'
    },
    {
      text: 'Percentage',
      value: 'percentage',
      align: 'center',
      width: '25%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getDiscussionStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changePrivacy() {
    if (
      confirm(
        'Are you sure you want to change the privacy of your dashboard? Current Privacy Mode: ' +
          this.stats?.privacy
      )
    ) {
      await this.$store.dispatch('loading');
      try {
        this.stats = await RemoteServices.changeDiscussionDashboardPrivacy();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
