<template>
  <a-layout-header class="header">
    <a-popconfirm
        title="Confirm your logout?"
        ok-text="Yes"
        cancel-text="Cancel"
        @confirm="logout()"
    >
      <a class="login-menu" v-show = "user.id">
        <span>Logout</span>
      </a>
    </a-popconfirm>
    <a class="login-menu" v-show = "user.id">
      <span>Hello: {{user.name}}</span>
    </a>
    <a class="login-menu" v-show = "!user.id" @click="showLoginModal">
      <span>Login</span>
    </a>
    <div class="logo">XX WIKI</div>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/">
        <router-link to="/">Home</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">About US</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" :style="user.id? {} : {display:'none'}">
        <router-link to="/admin/user">User Management</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook" :style="user.id? {} : {display:'none'}">
        <router-link to="/admin/ebook">Ebook Management</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category" :style="user.id? {} : {display:'none'}">
        <router-link to="/admin/category">Category Management</router-link>
      </a-menu-item>
    </a-menu>

    <a-modal v-model:open="loginModalVisible" title="login" :confirm-loading="loginModalLoading" @ok="login">
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="LoginName">
          <a-input v-model:value="loginUser.loginName"/>
        </a-form-item>
        <a-form-item label="Password">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
  import { defineComponent, ref, computed } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import store from '@/store';

  declare let hexMd5: any;
  declare let KEY: any;

  export default defineComponent({
    name: 'the-header',
    setup () {
      // for user information after login
      const user = computed(() => store.state.user);

      // for login
      const loginUser = ref({
        loginName: "test",
        password: "test"
      });
      const loginModalVisible = ref<boolean>(false);
      const loginModalLoading = ref<boolean>(false);
      const showLoginModal = () => {
        loginModalVisible.value = true;
      };

      // Login
      const login = () => {
        console.log("Login Started");
        loginModalLoading.value = true;
        loginUser.value.password = hexMd5(loginUser.value.password + KEY);
        axios.post('/user/login', loginUser.value).then((response) => {
          loginModalLoading.value = false;
          const data = response.data;
          if (data.success) {
            loginModalVisible.value = false;
            message.success("Login Successfully！");
            store.commit("setUser", data.content);
          } else {
            message.error(data.message);
          }
        });
      };

      // Logout
      const logout = () => {
        console.log("Logout Started");
        axios.get('/user/logout/' + user.value.token).then((response) => {
          const data = response.data;
          if (data.success) {
            message.success("Logout Successfully！");
            store.commit("setUser", {});
          } else {
            message.error(data.message);
          }
        });
      };

      return {
        loginModalVisible,
        loginModalLoading,
        showLoginModal,
        loginUser,
        login,
        user,
        logout
      }
    }
  });
</script>

<style>
.logo {
  width: 120px;
  height: 31px;
  /*background: rgba(255, 255, 255, 0.2);*/
  /*margin: 16px 28px 16px 0;*/
  float: left;
  color: white;
  font-size: 18px;
}
.login-menu {
  float: right;
  color: white;
  padding-left: 10px;
}
</style>