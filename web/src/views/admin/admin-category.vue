<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="Name">
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
          :data-source="categorys"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <template #bodyCell="{ column, text, record }">
          <template v-if="column.title == 'Cover'">
            <img :src="text" alt="avatar" />
          </template>
          <template v-if="column.title == 'Action'">
            <a-space size="small">
              <a-button type="primary" @click="edit(record)">
                Edit
              </a-button>
              <a-popconfirm
                  title="Once deleted, the category could not be restored. Confirm the deletion?"
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

  <a-modal v-model:open="modalOpen" title="Category Edit" :confirm-loading="confirmLoading" @ok="handleModalOk">
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Name">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="Parent Category">
        <a-input v-model:value="category.parent" />
      </a-form-item>
      <a-form-item label="Sort">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminCategory',
    setup() {
      const param = ref();
      param.value = {};
      const categorys = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: 'Name',
          dataIndex: 'name'
        },
        {
          title: 'Parent Category',
          key: 'parent',
          dataIndex: 'parent'
        },
        {
          title: 'Sort',
          dataIndex: 'sort'
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
        axios.get("/category/list", {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            categorys.value = data.content.list;
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

      const category = ref({});
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);

      /**
       * Edit category
       */
      const edit = (record : any) => {
        modalOpen.value = true;
        category.value = Tool.copy(record);
      };

      /**
       * Add a new category
       */
      const add = () => {
        modalOpen.value = true;
        category.value = {};
      };

      /**
       * Delete a category
       */
      const handleDelete = (id: number) => {
        axios.delete("/category/delete/" + id).then((response) => {
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

      const handleModalOk = () => {
        confirmLoading.value = true;
        axios.post("/category/save", category.value).then((response) => {
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

      onMounted(() => {
        handleQuery({
          page: 1,
          size: pagination.value.pageSize,
        });
      });

      return {
        param,
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        edit,
        add,

        category,
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