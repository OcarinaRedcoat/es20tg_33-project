<template>
  <div>
    <span v-html="convertMarkDown(question.content)" />
    <ul>
      <li v-for="option in question.options" :key="option.number">
        <span v-if="option.correct" v-html="convertMarkDown('**[★]** ')" />
        <span
          v-html="convertMarkDown(option.content)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
      </li>
    </ul>
    <br />
    <span class="font-weight-bold">Justification</span>
    <br />
    <span v-html="convertMarkDown(question.justification)" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StudentQuestion from '@/models/submissions/StudentQuestion';

@Component
export default class ShowStudentQuestion extends Vue {
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  convertMarkDown(text: string): string {
    return convertMarkDown(text, null);
  }

  hasJustification(question: StudentQuestion): boolean {
    return !(
      question.justification == null ||
      question.justification.trim().length == 0
    );
  }
}
</script>
