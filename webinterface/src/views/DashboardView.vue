<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->
<script setup>
import DashboardComponent from "@/components/DashboardComponent.vue";
import ConsoleComponent from "@/components/ConsoleComponent.vue";
import PlayerTableComponent from "@/components/PlayerTableComponent.vue";
import {IS_USER_AUTHENTICATED} from "@/store/storeconstants.js";
import LogoutComponent from "@/components/LogoutComponent.vue";
</script>
<script>
import {
  GET_CURRENTVIEW,
  GET_USERNAME,
  IS_USER_AUTHENTICATED,
} from "@/store/storeconstants.js";
import {addAlert} from "@/assets/js/globalmethods.js";

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
  <LogoutComponent/>
  <div v-if="isLoggedIn">
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
      <PlayerTableComponent v-if="currentView === 'PlayerTableComponent'"/>
    </div>
  </div>

</template>