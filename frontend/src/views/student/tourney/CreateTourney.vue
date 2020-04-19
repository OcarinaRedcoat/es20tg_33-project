<template>
  <div class="container">
    <h2>Create Tourney</h2>

    <edit-tourney-form/>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

import EditTourneyForm from '@/components/tourney/EditTourneyForm.vue';

@Component({
  components: { EditTourneyForm }
})
export default class CreateTourneys extends Vue {

}
</script>

<style lang="scss" scoped></style>
