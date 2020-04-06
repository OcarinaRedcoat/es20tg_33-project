<template>
  <div class="container">
    <h1>Submit Question</h1>

    <edit-student-question />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import EditStudentQuestion from '@/views/student/submissions/EditStudentQuestion.vue';

@Component({
  components: { EditStudentQuestion }
})

export default class CreateTourneys extends Vue {}
</script>

<style lang="scss" scoped></style>