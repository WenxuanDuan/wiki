<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
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
          <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
          >

            <template #bodyCell="{ column, text, record }">
              <template v-if="column.title == 'Name'">
                {{record.sort}} {{text}}
              </template>
              <template v-if="column.title == 'Action'">
                <a-space size="small">
                  <a-button type="primary" @click="edit(record)" size="small">
                    Edit
                  </a-button>
                  <a-popconfirm
                      title="Once deleted, the doc could not be restored. Confirm the deletion?"
                      ok-text="Delete"
                      cancel-text="Cancel"
                      @confirm="handleDelete(record.id)"
                  >
                    <a-button type="primary" danger size="small">
                      Delete
                    </a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  Save
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="Name"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  show-search
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="Please select parent doc"
                  allow-clear
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  tree-node-filter-prop="label"
                  :replaceFields="{label: 'name', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="Sort"/>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>

<!--  <a-modal v-model:open="modalOpen" title="Doc Edit" :confirm-loading="confirmLoading" @ok="handleModalOk">-->

<!--  </a-modal>-->

</template>

<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import { message, Modal } from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import { useRoute } from 'vue-router';
  import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
  import E from 'wangeditor'

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      const route = useRoute();
      console.log("Route: ", route);
      console.log("route.path: ", route.path);
      console.log("route.query: ", route.query);
      console.log("route.param: ", route.params);
      console.log("route.fullPath: ", route.fullPath);
      console.log("route.name: ", route.name);
      console.log("route.meta: ", route.meta);
      const param = ref();
      param.value = {};
      const docs = ref();
      const loading = ref(false);

      const columns = [
        {
          title: 'Name',
          dataIndex: 'name'
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

      /**
       * data query
       */
      const handleQuery = () => {
        loading.value = true;
        level1.value = [];
        axios.get("/doc/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            docs.value = data.content;
            console.log("Original Array: ", docs.value);

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("Tree Architecture: ", level1);
          }
          else {
            message.error(data.message);
          }
        });
      };

      const treeSelectData = ref();
      treeSelectData.value = [];
      const doc = ref({});
      const modalOpen = ref<boolean>(false);
      const confirmLoading = ref<boolean>(false);

      // initiate rich text
      let editor:any;
      const createEditor = () => {
        const editor = new E('#content');
        editor.config.zIndex = 0;
        editor.create();
      };

      /**
       * set one node and its children to disabled
       */
      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);

        // traverse array, i.e., traverse all nodes of one level
        for (let i = 0; i < treeSelectData.length; i ++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // if current node is target node
            console.log("disabled", node);
            // set target node to disabled
            node.disabled = true;

            // traverse all children and disable them
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j ++) {
                setDisable(children, children[j].id);
              }
            }
          }
          else {
            // if current node is not target node, then check its children
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };


      /**
       * find all nodes in the subtree
       */
      const deleteIds: Array<string> = [];
      const deleteNames: Array<string> = [];
      const getDeleteIds = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);

        // traverse array, i.e., traverse all nodes of one level
        for (let i = 0; i < treeSelectData.length; i ++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // if current node is target node
            console.log("delete", node);
            // put this node to result set
            deleteIds.push(id);
            deleteNames.push(node.name);

            // traverse all children
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j ++) {
                getDeleteIds(children, children[j].id);
              }
            }
          }
          else {
            // if current node is not the target node, then check its children
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };

      /**
       * Edit doc
       */
      const edit = (record : any) => {
        modalOpen.value = true;
        doc.value = Tool.copy(record);

        // could not choose current node and its children as parent node
        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        // add a null for select tree
        treeSelectData.value.unshift({id: 0, name: 'NULL'});

        // setTimeout(function () {
        //   if (editor == null) {
        //     createEditor();
        //   }
        //   else {
        //     editor.destroy();
        //     createEditor();
        //   }
        // });
      };

      /**
       * Add a new doc
       */
      const add = () => {
        modalOpen.value = true;
        doc.value = {
          ebookId: route.query.ebookId
        };

        treeSelectData.value = Tool.copy(level1.value);

        // add a NULL for select tree
        treeSelectData.value.unshift({id: 0, name: 'NULL'});

        // setTimeout(function () {
        //   if (editor == null) {
        //     createEditor();
        //   }
        //   else {
        //     editor.destroy();
        //     createEditor();
        //   }
        // });
      };

      /**
       * Delete a doc
       */
      const handleDelete = (id: number) => {
        // clear arrays, otherwise they will be increased continually
        deleteIds.length = 0;
        deleteNames.length = 0;

        // console.log(level1, level1.value, id);
        getDeleteIds(level1.value, id);

        Modal.confirm({
          title: 'Important Reminder',
          icon: createVNode(ExclamationCircleOutlined),
          content: 'Will delete: [' + deleteNames.join(", ") + "]. Could not recover once deleted. Confirm deletion? ",
          onOk() {
            // console.log(ids);
            axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
              const data = response.data; // data = commonResp
              if (data.success) {
                // reload current page
                handleQuery();
              }
            });
          },
        });
      };

      const handleSave = () => {
        confirmLoading.value = true;
        axios.post("/doc/save", doc.value).then((response) => {
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
        createEditor();
      });

      return {
        param,
        // docs,
        level1,
        columns,
        loading,
        handleQuery,

        edit,
        add,

        doc,
        modalOpen,
        confirmLoading,
        handleSave,

        handleDelete,

        treeSelectData
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