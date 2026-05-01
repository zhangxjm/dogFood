<template>
  <div class="products-container">
    <div class="page-header">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-card>
      <el-table :data="products" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="mainImage" label="图片" width="100">
          <template #default="{ row }">
            <el-avatar :size="50" :src="row.mainImage" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            <span class="original-price">¥{{ row.originalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="售价" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-input v-model="form.mainImage" placeholder="请输入图片URL" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">上架</el-radio>
                <el-radio :value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProducts, createProduct, updateProduct, deleteProduct } from '@/api/product'
import { getAllCategories } from '@/api/category'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const formRef = ref(null)

const categories = ref([])
const products = ref([])

const searchForm = reactive({
  name: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  categoryId: null,
  name: '',
  description: '',
  mainImage: '',
  originalPrice: 0,
  price: 0,
  stock: 0,
  status: 1
})

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  originalPrice: [{ required: true, message: '请输入原价', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await getAllCategories()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProducts({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    products.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadProducts()
}

const handleAdd = () => {
  dialogTitle.value = '新增商品'
  form.id = null
  form.categoryId = categories.value.length > 0 ? categories.value[0].id : null
  form.name = ''
  form.description = ''
  form.mainImage = ''
  form.originalPrice = 0
  form.price = 0
  form.stock = 0
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  form.id = row.id
  form.categoryId = row.categoryId
  form.name = row.name
  form.description = row.description || ''
  form.mainImage = row.mainImage || ''
  form.originalPrice = row.originalPrice
  form.price = row.price
  form.stock = row.stock
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProduct(row.id)
      ElMessage.success('删除成功')
      loadProducts()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateProduct(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createProduct(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadProducts()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadProducts()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  loadProducts()
}

onMounted(() => {
  loadCategories()
  products.value = [
    { id: 1, categoryId: 1, name: 'iPhone 15 Pro Max 256GB', description: 'A17 Pro芯片', mainImage: '', originalPrice: 9999, price: 8999, stock: 100, sales: 520, status: 1 },
    { id: 2, categoryId: 1, name: '华为Mate 60 Pro 512GB', description: '麒麟9000S芯片', mainImage: '', originalPrice: 7999, price: 6999, stock: 80, sales: 380, status: 1 },
    { id: 3, categoryId: 2, name: 'MacBook Pro 14英寸 M3 Pro', description: 'M3 Pro芯片', mainImage: '', originalPrice: 16999, price: 14999, stock: 50, sales: 120, status: 1 }
  ]
  pagination.total = 120
})
</script>

<style lang="scss" scoped>
.products-container {
  .page-header {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .original-price {
    color: #999;
    text-decoration: line-through;
  }
  
  .price {
    color: #FF4757;
    font-weight: bold;
  }
}
</style>
