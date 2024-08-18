<!--
  - Copyright (c) SimonPlays15 2024. All Rights Reserved
  -->

<template>
  <div v-if="!isLoggedIn" class="container mt-5 mb-5">
    <div class="d-flex justify-content-center align-items-center">
      <button class="btn btn-danger btn-lg w-75" @click="logout()">
        LOGOUT
      </button>
    </div>
  </div>
</template>