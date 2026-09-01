<template>
    <el-form ref="ruleFormRef" :model="formData">
      <el-row :gutter="24">
        <template v-for="item in formItemAttrs" :key="item.prop">
          <el-col v-bind="item.col">
            <el-form-item  :label="item.label" :prop="item.prop">
              <component v-model="formData[item.prop]" :is="isComp(item.comp)" :placeholder="item.placeholder" >
                <template v-if="item.comp === 'select'">
                  <el-option label="全部" value="1"></el-option>
                    <el-option
                      v-for="opt in item.options"
                      :key="opt.value"
                      :label="opt.label" 
                      :value="opt.value" />
                </template>
              </component>  
            </el-form-item>
          </el-col>
        </template>
      </el-row>
    <el-row>
    <el-button type="primary" @click="handleSearch">查询</el-button>
    <el-button @click="handleReset(ruleFormRef)">重置</el-button>
     </el-row>
    </el-form>
</template>

<script setup>
import { ref, reactive, computed} from 'vue'
import { ElButton } from 'element-plus';

// 表单数据
const ruleFormRef = ref()
const formData = reactive({})

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = ''
  })
}

const emit = defineEmits(['search'])

const formItemAttrs = computed(() => {
  const {formitem} = props
  formitem.forEach(item => {
    item.col={ xs:24 ,sm:12 ,md:8 ,lg:6 ,xl:6}
  })
  return formitem
})

const props = defineProps({
  formitem: {
    type: Array,
    default: () => []
  }
})

const isComp = (comp) => {
  return{
    input:'elInput',
    select:'elSelect'
  }[comp]
}

const handleSearch = () => {
  // console.log(formData)
  emit('search', formData)
}

const handleReset = (formEl) => {
  // 先重置表单，再触发查询
  if(!formEl) return
  formEl.resetFields()
  emit('search', formData)
}

</script>