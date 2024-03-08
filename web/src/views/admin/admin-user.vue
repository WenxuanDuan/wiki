<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="LoginName">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              Search
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              Add
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <template #bodyCell="{ column, text, record }">
          <template v-if="column.title == 'Action'">
            <a-space size="small">
              <a-button type="primary" @click="edit(record)">
                Edit
              </a-button>
              <a-popconfirm
                  title="Once deleted, the user could not be restored. Confirm the deletion?"
                  ok-text="Delete"
                  cancel-text="Cancel"
                  @confirm="handleDelete(record.id)"
              >
                <a-button type="primary" danger>
                  Delete
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal v-model:open="modalOpen" title="User Management" :confirm-loading="confirmLoading" @ok="handleModalOk">
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="LoginName">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="Name">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="Password" v-show="!user.id">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";

  declare let hexMd5: any;
  declare let KEY: any;

  export default defineComponent({
    name: 'AdminUser',
    setup() {
      const param = ref();
      param.value = {};
      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: 'LoginName',
          dataIndex: 'loginName'
        },
        {
          title: 'Name',
          dataIndex: 'name'
        },
        {
          title: 'Password',
          dataIndex: 'password'
        },
        {
          title: 'Action',
          key: 'action'
        }
      ];

      /**
       * data query
       **/
      const handleQuery = (params: any) => {
        loading.value = true;
        users.value = [];
        axios.get("/user/list", {
          params: {
            page: params.page,
            size: params.size,
            loginName: param.value.loginName
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            users.value = data.content.list;

            // reset pages button
            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          }
          else {
            message.error(data.message);
          }
        });
      };

      /**
       * trigger when clicking page
       */
      const handleTableChange = (pagination: any) => {
        console.log("Default pagination parameters：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      // ---------- Form --------
      /**
       * array, [100, 101], i.e., 前端开发 / Vue
       */
      const user = ref();
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);
      const handleModalOk = () => {
        confirmLoading.value = true;

        user.value.password = hexMd5(user.value.password + KEY);

        axios.post("/user/save", user.value).then((response) => {
          confirmLoading.value = false;
          const data = response.data; // data = commonResp
          if (data.success) {
            modalOpen.value = false;

            // reload current page
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          }
          else {
            message.error(data.message);
          }
        });
      };

      /**
       * Edit user
       */
      const edit = (record : any) => {
        modalOpen.value = true;
        user.value = Tool.copy(record);
      };

      /**
       * Add a new user
       */
      const add = () => {
        modalOpen.value = true;
        user.value = {};
      };

      /**
       * Delete a user
       */
      const handleDelete = (id: number) => {
        axios.delete("/user/delete/" + id).then((response) => {
          const data = response.data; // data = commonResp
          if (data.success) {
            // reload current page
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          }
        });
      };

      onMounted(() => {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize,
        });
      });

      return {
        param,
        users,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        edit,
        add,

        user,
        modalOpen,
        confirmLoading,
        handleModalOk,

        handleDelete
      }
    }
  });
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>