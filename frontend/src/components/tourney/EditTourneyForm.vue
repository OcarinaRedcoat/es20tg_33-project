<template>
  <v-card>
    <v-container>
      <v-form ref="form" v-model="valid" lazy-validation data-cy="form">
        <v-row justify="space-between">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="editTourney.tourneyTitle"
              :rules="[v => !!v || 'Must have a title']"
              label="Title"
              data-cy="title"
              required
            ></v-text-field>

            <v-text-field
              v-model="editTourney.tourneyNumberOfQuestions"
              type="number"
              :rules="[v => v > 0 || 'Number must be positive']"
              label="Number of Questions"
              value="0"
              data-cy="numberOfQuestions"
              required
            ></v-text-field>
          </v-col>

          <v-col cols="12" md="6">

            <VueCtkDateTimePicker
              label="*Available Date"
              v-model="availableDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="availableDate"
            ></VueCtkDateTimePicker>

            <VueCtkDateTimePicker
              label="*Conclusion Date"
              v-model="conclusionDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="conclusionDate"
            ></VueCtkDateTimePicker>

          </v-col>
        </v-row>

        <v-list>
          <v-list-item-group v-model="editTourney.tourneyTopics" multiple data-cy="topics">
            <template v-for="(topic, i) in topics">
              <v-divider v-if="!topic" :key="`divider-${i}`"></v-divider>

              <v-list-item
                v-else
                :key="`topic-${i}`"
                :value="topic"
                active-class="deep-purple--text text--accent-4"
              >
                <template v-slot:default="{ active, toggle }">
                  <v-list-item-content>
                    <v-list-item-title v-text="topic.name"></v-list-item-title>
                  </v-list-item-content>

                  <v-list-item-action>
                    <v-checkbox
                      :input-value="active"
                      :true-value="topic"
                      color="deep-purple accent-4"
                      @click="toggle"
                    ></v-checkbox>
                  </v-list-item-action>
                </template>
              </v-list-item>
            </template>
          </v-list-item-group>
        </v-list>

        <v-btn color="error" class="mr-4" @click="reset">Clear</v-btn>

        <v-btn :disabled="!valid" color="success" class="mr-4" data-cy="saveTourney" @click="saveTourney">Save</v-btn>
      </v-form>
    </v-container>
  </v-card>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '../../services/RemoteServices';
import Tourney from '../../models/tourney/Tourney';
import Course from '../../models/user/Course';
import Topic from '../../models/management/Topic';

@Component
export default class EditTourneyForm extends Vue {
  editTourney!: Tourney;
  valid: boolean = false;
  availableDate: string = '';
  conclusionDate: string = '';
  topics: Topic[] = [];

  async created() {
    this.editTourney = new Tourney();
    this.valid = false;
    this.topics = await this.getTopics();
    this.editTourney.tourneyCourseExecution = await this.getCourse();
  }

  @Watch('availableDate', { deep: true })
  formatAvailableDate() {
    this.editTourney.tourneyAvailableDate = this.availableDate;
  }

  @Watch('conclusionDate', { deep: true })
  formatConclusionDate() {
    this.editTourney.tourneyConclusionDate = this.conclusionDate;
  }

  hasAllValues() {
    return this.editTourney.tourneyTitle;
  }

  async getCourse() {
    const course = this.$store.getters.getCurrentCourse;
    if (course == null) {
      await this.$store.dispatch('error', 'Can\'t find course');
    }
    return course;
  }

  async getTopics() {
    try {
      return await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    return [];
  }

  toggle() {}

  async saveTourney() {
    if (
      !(
        this.editTourney.tourneyTitle ||
        this.editTourney.tourneyAvailableDate ||
        this.editTourney.tourneyConclusionDate ||
        this.editTourney.tourneyNumberOfQuestions ||
        this.editTourney.tourneyTopics?.length == 0
      )
    ) {
      await this.$store.dispatch('error', 'Please fill the form');
      return;
    } else {
      try {
        const result = await RemoteServices.createTourney(this.editTourney);
        this.$emit('new-tourney', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  reset() {
    (this.$refs.form as Vue & { reset: () => boolean }).reset();
    this.editTourney = new Tourney();
  }
}
</script>


<style lang="css" scoped>
.date-time-picker {
  padding: 8px 0 20px;
}
</style>