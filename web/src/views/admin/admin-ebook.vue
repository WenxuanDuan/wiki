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
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <template #bodyCell="{ column, text, record }">
          <template v-if="column.title == 'Cover'">
            <img :src="text" alt="avatar" />
          </template>
          <template v-if="column.title == 'Category'">
            <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
          </template>
          <template v-if="column.title == 'Action'">
            <a-space size="small">
              <router-link :to="'/admin/doc?ebookId=' + record.id">
                <a-button type="primary">
                  Doc Management
                </a-button>
              </router-link>
              <a-button type="primary" @click="edit(record)">
                Edit
              </a-button>
              <a-popconfirm
                  title="Once deleted, the ebook could not be restored. Confirm the deletion?"
                  ok-text="Delete"
                  cancel-text="Cancel"
                  @confirm="handleDelete(record.id)"
              >
              </a-popconfirm>
              <a-button type="primary" danger>
                Delete
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal v-model:open="modalOpen" title="Ebook Edit" :confirm-loading="confirmLoading" @ok="handleModalOk">
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Cover">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="Name">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="Category">
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            :options="level1"
        />
      </a-form-item>
      <a-form-item label="Description">
        <a-input v-model:value="ebook.description" type="textarea" />
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
    name: 'AdminEbook',
    setup() {
      const param = ref();
      param.value = {};
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: 'Cover',
          dataIndex: 'cover'
        },
        {
          title: 'Name',
          dataIndex: 'name'
        },
        {
          title: 'Category',
          dataIndex: { customRender: 'category' }
        },
        {
          title: 'DocumentCount',
          dataIndex: 'docCount'
        },
        {
          title: 'View',
          dataIndex: 'viewCount'
        },
        {
          title: 'Like',
          dataIndex: 'voteCount'
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
        ebooks.value = [];
        axios.get("/ebook/list", {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            ebooks.value = data.content.list;
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
      const categoryIds = ref();
      const ebook = ref();
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);
      const handleModalOk = () => {
        confirmLoading.value = true;
        ebook.value.category1Id = categoryIds.value[0];
        ebook.value.category2Id = categoryIds.value[1];
        axios.post("/ebook/save", ebook.value).then((response) => {
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
       * Edit ebook
       */
      const edit = (record : any) => {
        modalOpen.value = true;
        ebook.value = Tool.copy(record);
        categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
      };

      /**
       * Add a new ebook
       */
      const add = () => {
        modalOpen.value = true;
        ebook.value = {};
      };

      /**
       * Delete a ebook
       */
      const handleDelete = (id: number) => {
        axios.delete("/ebook/delete/" + id).then((response) => {
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

      const level1 = ref();
      let categorys: any;
      /**
       *  query all categories
       */
      const handleQueryCategory = () => {
        loading.value = true;
        axios.get("/category/all").then((response) => {
          loading.value = true;
          const data = response.data;
          if (data.success) {
            categorys = data.content;
            console.log("Original Array: ", categorys);

            level1.value = [];
            level1.value = Tool.array2Tree(categorys, 0);
            console.log("Tree Category: ", level1.value);

            // load ebooks after categories
            handleQuery({
              page: 1,
              size: pagination.value.pageSize,
            });
          }
          else {
            message.error(data.message);
          }
        })
      };

      const getCategoryName = (cid: number) => {
        let result = "";
        categorys.forEach((item: any) => {
          if (item.id === cid) {
            result = item.name;
          }
        });
        return result;
      };

      onMounted(() => {
        handleQueryCategory();
      });

      return {
        param,
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,
        getCategoryName,

        edit,
        add,

        ebook,
        modalOpen,
        confirmLoading,
        handleModalOk,
        categoryIds,
        level1,

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