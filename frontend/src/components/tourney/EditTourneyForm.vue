<template>
  <v-card>
    <v-container>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-row justify="space-between">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="editTourney.tourneyTitle"
              :rules="[v => !!v || 'Must have a title']"
              label="Title"
              required
            ></v-text-field>

            <v-text-field
              v-model="editTourney.tourneyNumberOfQuestions"
              type="number"
              :rules="[v => v > 0 || 'Number must be positive']"
              label="Number of Questions"
              value="0"
              required
            ></v-text-field>
          </v-col>

          <v-col cols="12" md="6">
            <v-datetime-picker
              label="Available Date"
              format="yyyy-MM-dd HH:mm"
              v-model="availableDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
            ></v-datetime-picker>

            <v-datetime-picker
              label="Conclusion Date"
              v-model="conclusionDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
            ></v-datetime-picker>
          </v-col>
        </v-row>

        <v-list>
          <v-list-item-group v-model="editTourney.tourneyTopics" multiple>
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

        <v-btn :disabled="!valid" color="success" class="mr-4" @click="saveTourney">Create</v-btn>
      </v-form>
    </v-container>
  </v-card>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import { formatUTCDate } from '../../utils';
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
    this.editTourney.tourneyAvailableDate = formatUTCDate(
      this.availableDate
    );
  }

  @Watch('conclusionDate', { deep: true })
  formatConclusionDate() {
    this.editTourney.tourneyConclusionDate = formatUTCDate(
      this.conclusionDate
    );
    console.log(this.editTourney.tourneyConclusionDate);
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
    this.$refs.form.reset();
    this.editTourney = new Tourney();
  }
}
</script>
