<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<script>
export default {
  data() {
    return {
      users: [{
        username: "SimonPlays15",
        uuid: "bc3912479-sdfgds23--dfs--g234--",
        lastSeen: "01.01.1970 00:00:00",
        online: false
      }]
    }
  }
}
</script>

<template>
  <div class="row g-0">
    <table class="table table-responsive table-striped table-responsive table-hover">
      <thead class="text-center fw-bold">
      <tr>
        <td>Username</td>
        <td>UUID</td>
        <td>Last Seen</td>
        <td>Online?</td>
      </tr>
      </thead>
      <tbody class="table-group-divider align-middle">
      <tr v-for="user in users">
        <td>{{ user.username }}</td>
        <td>{{ user.uuid }}</td>
        <td>{{ user.lastSeen }}</td>
        <td :class="(user.online ? 'bg-success':'bg-danger')+' text-center'">{{ user.online ? 'Yes' : 'No' }}</td>
        <td class="text-center">
          <div aria-label="User actions" class="btn-group" role="group">
            <button class="btn btn-outline-light" type="button">
              Ban
            </button>
            <button :disabled="!user.online" class="btn btn-outline-light" type="button">
              Kick
            </button>
            <button :disabled="!user.online" class="btn btn-outline-light" type="button">
              Mute
            </button>
            <button :disabled="!user.online" class="btn btn-outline-light" type="button">
              Warn
            </button>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>

</style>