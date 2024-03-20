<template>
  <a-layout-footer style="text-align: center">
    XX WIKI<span v-show="user.id">, Welcome: {{user.name}}</span>
  </a-layout-footer>
</template>

<script lang="ts">
  import {computed, defineComponent, onMounted } from 'vue';
  import store from "@/store";
  import { Tool } from '@/util/tool';

  export default defineComponent({
    name: 'the-footer',
    setup() {
      const user = computed(() => store.state.user);

      let websocket: any;
      let token: any;
      const onOpen = () => {
        console.log('WebSocket connected successfully，state：', websocket.readyState)
      };
      const onMessage = (event: any) => {
        console.log('WebSocket received message：', event.data);
      };
      const onError = () => {
        console.log('WebSocket connection failed，state：', websocket.readyState)
      };
      const onClose = () => {
        console.log('WebSocket connection closed，state：', websocket.readyState)
      };
      const initWebSocket = () => {
        // connected successfully
        websocket.onopen = onOpen;
        // received message
        websocket.onmessage = onMessage;
        // connection error
        websocket.onerror = onError;
        // cnnection closed
        websocket.onclose = onClose;
      };

      onMounted(() => {
        // WebSocket
        if ('WebSocket' in window) {
          token = Tool.uuid(10);
          // connected address：ws://127.0.0.1:8880/ws/xxx
          websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
          initWebSocket()

          // close
          // websocket.close();
        } else {
          alert('Current browser is not supported')
        }
      });

      return {
        user
      }
    }
  });
</script>