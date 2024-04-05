<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              Refresh
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              Add
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <p>
        <a-alert
            class="tip"
            message="Tips: The categories here will be displayed in the sub-menu in the homepage"
            type="info"
            closable
        />
      </p>
      <a-table
          v-if="level1.length > 0"
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
          :defaultExpandAllRows="true"
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
        <a-select
            ref="select"
            v-model:value="category.parent"
        >
          <a-select-option :value="0">
            NULL
          </a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
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
      const loading = ref(false);

      const columns = [
        {
          title: 'Name',
          dataIndex: 'name'
        },
        // {
        //   title: 'Parent Category',
        //   key: 'parent',
        //   dataIndex: 'parent'
        // },
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
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref();
      level1.value = [];

      /**
       * data query
       */
      const handleQuery = () => {
        loading.value = true;
        level1.value = [];
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            categorys.value = data.content;
            console.log("Original Array: ", categorys.value);

            level1.value = [];
            level1.value = Tool.array2Tree(categorys.value, 0);
            console.log("Tree Architecture: ", level1);
          }
          else {
            message.error(data.message);
          }
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
            handleQuery();
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
            handleQuery();
          }
          else {
            message.error(data.message);
          }
        });
      };

      onMounted(() => {
        handleQuery();
      });

      return {
        param,
        // categorys,
        level1,
        columns,
        loading,
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