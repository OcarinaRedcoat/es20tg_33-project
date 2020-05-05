<template>
  <div class="container">
    <h2>Tourneys Dashboard</h2>
		<results-list/>
	</div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import ResultsList from '../../../components/tourney/ResultsList.vue';


  @Component({
    components: { ResultsList }
  })
  export default class TourneysDashboard extends Vue {

  }
</script>

<style lang="scss" scoped></style>
