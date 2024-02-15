<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
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
          <template v-if="column.title == 'Action'">
            <a-space size="small">
              <a-button type="primary" @click="edit(record)">
                Edit
              </a-button>
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
      <a-form-item label="Category 1">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="Category 2">
        <a-input v-model:value="ebook.category2Id" />
      </a-form-item>
      <a-form-item label="Description">
        <a-input v-model:value="ebook.desc" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
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
          title: 'Category1',
          key: 'category1Id',
          dataIndex: 'category1Id'
        },
        {
          title: 'Category2',
          dataIndex: 'category2Id'
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
        axios.get("/ebook/list", {
          params: {
            page: params.page,
            size: params.size
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          ebooks.value = data.content.list;

          // reset pages button
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
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

      const ebook = ref({});
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);

      const edit = (record : any) => {
        modalOpen.value = true;
        ebook.value = record
      };

      const handleModalOk = () => {
        confirmLoading.value = true;
        axios.post("/ebook/save", ebook.value).then((response) => {
          const data = response.data; // data = commonResp
          if (data.success) {
            modalOpen.value = false;
            confirmLoading.value = false;

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
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,
        edit,
        ebook,
        modalOpen,
        confirmLoading,
        handleModalOk
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