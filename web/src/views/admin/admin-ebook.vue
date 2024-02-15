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
              <a-button type="primary" @click="edit">
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
    <p>{{ modalText }}</p>
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

      const modalText = ref<string>('Test');
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);

      const edit = () => {
        modalOpen.value = true;
      };

      const handleModalOk = () => {
        modalText.value = 'The modal will be closed after two seconds';
        confirmLoading.value = true;
        setTimeout(() => {
          modalOpen.value = false;
          confirmLoading.value = false;
        }, 2000);
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
        modalText,
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