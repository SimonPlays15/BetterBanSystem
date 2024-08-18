<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script setup>
</script>

<script>
import axios from "axios";
import {GET_TOKEN} from "@/store/storeconstants.js";

export default {
  data() {
    return {
      playerActions: [],
      serverInformations: {
        maxPlayers: "??",
        onlinePlayers: "??",
        bannedPlayers: "??",
        mutedPlayers: "??"
      }
    }
  },
  methods: {
    updateDashboard: function () {
      setTimeout(() => {
        const call = axios.post("http://localhost:8080/api/v1", {}, {
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${this.$store.getters[`auth/${GET_TOKEN}`]}`
          }
        });
      }, 5000);
    }
  },
  created: function () {
    //this.updateDashboard()
  }
}

</script>

<template>
  <div class="row row-cols-3 d-flex justify-content-center">
    <div class="col col-3">
      <div class="card" style="border-radius: 1rem;">
        <div class="card-body">
          <div class="row g-0">
            <div class="col-md-4 d-flex justify-content-center align-items-center card bg-primary"
                 style="margin-right: 6px;">
              <div class="card-body">
                <i class="bi bi-people-fill" style="font-size: 30px;"></i>
              </div>
            </div>
            <div class="col d-flex justify-content-center align-items-center">
              <div class="row row-cols-1">
                <div class="col">
                  <span class="text-center fw-bold">Online Players:</span>
                </div>
                <div class="col">
                  <span id="onlinePlayers" class="text-danger">{{ serverInformations.onlinePlayers }}</span>/<span
                    id="maxPlayers">{{ serverInformations.maxPlayers }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col col-3">
      <div class="card" style="border-radius: 1rem;">
        <div class="card-body">
          <div class="row g-0">
            <div class="col-md-4 d-flex justify-content-center align-items-center card bg-danger"
                 style="margin-right: 6px;">
              <div class="card-body">
                <i class="bi bi-person-fill-slash" style="font-size: 30px;"></i>
              </div>
            </div>
            <div class="col d-flex justify-content-center align-items-center">
              <div class="row row-cols-1">
                <div class="col">
                  <span class="text-center fw-bold">Banned Players</span>
                </div>
                <div class="col">
                  <span id="onlinePlayers">{{ serverInformations.bannedPlayers }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col col-3">
      <div class="card" style="border-radius: 1rem;">
        <div class="card-body">
          <div class="row g-0">
            <div class="col-md-4 d-flex justify-content-center align-items-center card bg-danger"
                 style="margin-right: 6px;">
              <div class="card-body">
                <i class="bi bi-mic-mute-fill" style="font-size: 30px;"></i>
              </div>
            </div>
            <div class="col d-flex justify-content-center align-items-center">
              <div class="row row-cols-1">
                <div class="col">
                  <span class="text-center fw-bold">Muted Players</span>
                </div>
                <div class="col">
                  <span id="onlinePlayers">{{ serverInformations.mutedPlayers }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="container mt-5">
    <table class="table table-responsive table-striped table-hover table-bordered caption-top">
      <caption>Player actions</caption>
      <thead>
      <tr>
        <th scope="col">Player</th>
        <th scope="col">By</th>
        <th scope="col">Action</th>
        <th scope="col">Timestamp</th>
      </tr>
      </thead>
      <tbody class="table-group-divider">
      <tr v-for="action in playerActions" :key="action.timestamp">
        <td>{{ action.player }}</td>
        <td>{{ action.by }}</td>
        <td>{{ action.action }}</td>
        <td>{{ action.timestamp }}</td>
      </tr>
      </tbody>
    </table>
  </div>

</template>

<style scoped>

</style>