<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->
<script setup>
import DashboardComponent from "@/components/DashboardComponent.vue";
import ConsoleComponent from "@/components/ConsoleComponent.vue";
</script>
<script>
import {GET_CURRENTVIEW} from "@/store/storeconstants.js";

export default {
  data() {
    return {
      currentView: this.$store.getters[`navigation/${GET_CURRENTVIEW}`]
    }
  },
  updated() {
    this.currentView = this.$store.getters[`navigation/${GET_CURRENTVIEW}`]
  }
}
</script>


<template>
  <div class="container-fluid">
    <div class="mb-4">
      <div class="row row-cols-auto d-flex justify-content-between align-content-center">
        <div class="col">
          <h4 class="fw-bold">Admin Dashboard</h4>
        </div>
        <div class="col">
          <button class="btn btn-danger">
            <a class="link-light link-underline-opacity-0"
               href="https://github.com/SimonPlays15/BetterBanSystem/issues/new/choose"
               target="_blank">
              Bugreport <i class="bi bi-github"></i>
            </a>
          </button>
        </div>
      </div>
    </div>
  </div>

  <div class="container">
    <DashboardComponent v-if="currentView === 'DashboardComponent'"/>
    <ConsoleComponent v-if="currentView === 'ConsoleComponent'"/>
  </div>

</template>