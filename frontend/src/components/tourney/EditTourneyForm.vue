<template>
  <v-card>
    <v-container>
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-row justify="space-between">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="editTourney.tourneyName"
              :rules="[v => !!v || 'Must have a Name']"
              label="Name"
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
              v-model="editTourney.tourneyAvailableDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
              :rule="[dateRule]"
            ></v-datetime-picker>

            <v-datetime-picker
              label="Conclusion Date"
              v-model="editTourney.tourneyConclusionDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
              :rule="[dateRule]"
            ></v-datetime-picker>
          </v-col>
        </v-row>

        <v-list shaped>
          <v-list-item-group v-model="selectedTopics" multiple>
            <template v-for="(item, i) in topics">
              <v-divider v-if="!item" :key="`divider-${i}`"></v-divider>

              <v-list-item
                v-else
                :key="`topic-${i}`"
                :value="item"
                active-class="deep-purple--text text--accent-4"
              >
                <template v-slot:default="{ active, toggle }">
                  <v-list-item-content>
                    <v-list-item-title v-text="item"></v-list-item-title>
                  </v-list-item-content>

                  <v-list-item-action>
                    <v-checkbox
                      :input-value="active"
                      :true-value="item"
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
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '../../services/RemoteServices';
import Tourney from '../../models/tourney/Tourney';
import Course from '../../models/user/Course';
import Topic from '../../models/management/Topic';

@Component
export default class EditTourneyForm extends Vue {
  editTourney!: Tourney;
  valid: boolean = false;
  courseExecutionItems!: Course[];
  currentCourse!: Course;
  topics: Topic[] = [];
  selectedTopics: Topic[] = [];
  courses!: Course[];

  async created() {
    this.editTourney = new Tourney();
    this.valid = false;
    this.topics = await this.getTopics();
  }

  dateRule(value: string) {
    return !!value || 'Not a valid Date';
  }

  hasAllValues() {
    return this.editTourney.tourneyName;
  }

  async getTopics() {
    try {
      return await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    return [];
  }

  async saveTourney() {
    if (!this.editTourney) {
      // await this.$store.dispatch(
      //   'error',
      //   'Tourney must have name'
      // );
      return;
    }

    // if (this.editTourney) {
    //   try {
    //     const result = await RemoteServices.createCourse(this.editTourney);
    //     this.$emit('new-tourney', result);
    //   } catch (error) {
    //     await this.$store.dispatch('error', error);
    //   }
    // }
  }

  reset() {}
}
</script>
