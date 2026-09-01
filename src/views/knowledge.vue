<template>
  <div>
    <PageHead title="知识文章">
      <template #buttons>
        <el-button type="primary">新增</el-button>
      </template>
    </PageHead>
    <TableSearch :formitem="formitem" @search="handleSearch" />
  </div>
</template>
<!-- 知识文章页面标题 -->
<script setup>
import PageHead from '@/components/PageHead.vue'
import TableSearch from '@/components/TableSearch.vue'

const formitem = [
  { comp: 'input', prop: 'title' ,label: '标题',placeholder:'请输入标题'},
  { comp: 'select', prop: 'categoryId' ,label: '分类',placeholder:'请选择分类',options:[{
    label:'情感知识',
    value:'1'
  },
{
    label:'心理健康基础',
    value:'2'
  }]},
]
const handleSearch = (formData) => {
  console.log(formData,'查询参数')
}
</script>