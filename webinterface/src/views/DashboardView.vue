<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->
<script setup>
import DashboardComponent from "@/components/DashboardComponent.vue";
import ConsoleComponent from "@/components/ConsoleComponent.vue";
import PlayerTableComponent from "@/components/PlayerTableComponent.vue";
import {IS_USER_AUTHENTICATED} from "@/store/storeconstants.js";
import {logout} from "@/assets/js/globalmethods.js";
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
  computed: {
    isLoggedIn() {
      // TODO UNCOMMENT
      return true;
      //return this.$store.getters[`auth/${GET_USERNAME}`] && this.$store.getters[`auth/${IS_USER_AUTHENTICATED}`];
    }
  },
  mounted() {
    if (!this.$store.getters[`auth/${GET_USERNAME}`] && this.$store.getters[`auth/${IS_USER_AUTHENTICATED}`]) {
      addAlert("danger", "Error", "Failed to load the username are you really logged in?", false, false)
    }
  },
  updated() {
    this.currentView = this.$store.getters[`navigation/${GET_CURRENTVIEW}`]
  }
}
</script>


<template>
  <div v-if="!isLoggedIn">
    <button class="btn btn-danger btn-lg w-100" @click="logout($store)">
      LOGOUT
    </button>
  </div>
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