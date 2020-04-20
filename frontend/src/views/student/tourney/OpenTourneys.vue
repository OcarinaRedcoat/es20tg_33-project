<template>
  <div class="container">
    <h2>Open Tourneys List</h2>
    <tourney-list/>
  </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import TourneyList from '../../../components/tourney/TourneyList.vue';


  @Component({
    components: { TourneyList }
  })
  export default class OpenTourneys extends Vue {

  }
</script>

<style lang="scss" scoped></style>
