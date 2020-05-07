<template>
  <ul data-cy="dashboardList">
    <li class="list-header" >
      <div class="col" v-for="column in headersProp" :key="column.name">{{ column.name }}</div>
    </li>
    <li class="list-row" v-for="elem in elementsProp" :key="elem.id">
      <div class="col" v-for="column in headersProp" :key="column.name">{{ elem[column.value] }}</div>
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';

@Component
export default class DashboardList extends Vue {
  @Prop({required: true})
  headers: object[] | undefined;
  @Prop({required: true})
  elements: object[] | undefined;

  get headersProp() {
      return this.headers;
  };

  get elementsProp() {
      return this.elements;
  };

}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      width: 25%;
    }

    .last-col {
      max-width: 50px !important;
    }

    .list-row {
      background-color: #ffffff;
      cursor: pointer;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
    }

    .list-row:hover {
      background-color: #c8c8c8;
    }
  }
}
</style>