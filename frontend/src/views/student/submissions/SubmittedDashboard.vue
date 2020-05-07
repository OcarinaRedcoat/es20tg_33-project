<template>
    <div class="container">
        <h2>Statistics</h2>
        <div v-if="stats != null" class="stats-container">
            <v-switch v-model="private" class="mx-2" @click="toggleStudentQuestionPrivacy" data-cy="dashboardPrivacy"></v-switch>
            <p>{{privacy}}</p>
            <div class="items">
                <div class="icon-wrapper" ref="totalQuizzes">
                    <animated-number :number="stats.submitted" />
                </div>
                <div class="project-name">
                    <p>Total Questions Submitted</p>
                </div>
            </div>
            <div class="items">
                <div class="icon-wrapper" ref="totalAnswers">
                    <animated-number :number="stats.approved" />
                </div>
                <div class="project-name">
                    <p>Total Questions Approved</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import StudentQuestionStats from '@/models/submissions/StudentQuestionStats';
  import RemoteServices from '@/services/RemoteServices';
  import AnimatedNumber from '@/components/AnimatedNumber.vue';

  @Component({
    components: { AnimatedNumber }
  })
  export default class SubmittedDashboard extends Vue {
    stats: StudentQuestionStats | null = null;
    private: boolean = true;

    async created() {
        this.stats = await RemoteServices.getStudentQuestionDashboard();
        this.private = await RemoteServices.toggleStudentQuestionPrivacy(this.privacy);
    }

    get privacy(){
      return !this.private?"private":"public";
    }

    async toggleStudentQuestionPrivacy() {
      if (confirm("Are you sure you want to change this dashboard's privacy settings?")) {
        try {
          this.private = await RemoteServices.toggleStudentQuestionPrivacy(this.privacy);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
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
