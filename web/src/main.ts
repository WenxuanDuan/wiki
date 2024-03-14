import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";

axios.defaults.baseURL = process.env.VUE_APP_SERVER;

/**
 * axios interceptor
 */
axios.interceptors.request.use(function (config) {
    console.log('request parameter：', config);
    const token = store.state.user.token;
    if (Tool.isNotEmpty(token)) {
        config.headers.token = token;
        console.log("request to add token for headers:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    console.log('return results：', response);
    return response;
}, error => {
    console.log('return error：', error);
    return Promise.reject(error);
});


const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app')

// use icons for whole project
const icons : any  = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}

console.log('Environment: ', process.env.NODE_ENV);
console.log('Server-Side: ', process.env.VUE_APP_SERVER);

