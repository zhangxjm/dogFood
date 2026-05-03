<template>
  <el-container>
    <Header />
    <el-main>
      <div class="admin-container">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">管理后台</span>
              <el-radio-group v-model="activeTab">
                <el-radio-button value="products">商品审核</el-radio-button>
                <el-radio-button value="users">用户管理</el-radio-button>
                <el-radio-button value="categories">分类管理</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          
          <div v-if="activeTab === 'products'">
            <el-table
              :data="productList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="coverImage" label="商品图片" width="100">
                <template #default="scope">
                  <el-image
                    :src="scope.row.coverImage || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                    :fit="cover"
                    class="table-image"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="title" label="商品标题" min-width="200">
                <template #default="scope">
                  <div class="title-cell">
                    <div class="title">{{ scope.row.title }}</div>
                    <div class="price">¥{{ scope.row.price }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="userNickname" label="发布者" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="发布时间" width="180">
                <template #default="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="280" fixed="right">
                <template #default="scope">
                  <el-button type="primary" link @click="viewProduct(scope.row.id)">
                    查看详情
                  </el-button>
                  <el-button
                    type="success"
                    link
                    v-if="scope.row.status === 'pending'"
                    @click="auditProduct(scope.row, 'active')"
                  >
                    通过
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    v-if="scope.row.status === 'pending'"
                    @click="auditProduct(scope.row, 'rejected')"
                  >
                    驳回
                  </el-button>
                  <el-button
                    type="warning"
                    link
                    v-if="scope.row.status === 'active'"
                    @click="takeDownProduct(scope.row)"
                  >
                    下架
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-empty v-if="productList.length === 0 && !loading" description="暂无待审核商品" />
            
            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
          
          <div v-if="activeTab === 'users'">
            <el-table
              :data="userList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="avatar" label="头像" width="80">
                <template #default="scope">
                  <el-avatar
                    :size="40"
                    :src="scope.row.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="nickname" label="昵称" width="120" />
              <el-table-column prop="phone" label="手机号" width="140" />
              <el-table-column prop="email" label="邮箱" min-width="180" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'primary'">
                    {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="注册时间" width="180">
                <template #default="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button
                    :type="scope.row.status === 1 ? 'warning' : 'success'"
                    link
                    v-if="scope.row.role !== 'admin'"
                    @click="toggleUserStatus(scope.row)"
                  >
                    {{ scope.row.status === 1 ? '禁用' : '启用' }}
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    v-if="scope.row.role !== 'admin'"
                    @click="deleteUser(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-empty v-if="userList.length === 0 && !loading" description="暂无用户" />
          </div>
          
          <div v-if="activeTab === 'categories'">
            <el-button type="primary" style="margin-bottom: 20px" @click="openCategoryDialog()">
              <el-icon><Plus /></el-icon>
              添加分类
            </el-button>
            
            <el-table
              :data="categoryList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="分类名称" width="200" />
              <el-table-column prop="sort" label="排序" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180">
                <template #default="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button type="primary" link @click="openCategoryDialog(scope.row)">
                    编辑
                  </el-button>
                  <el-button type="danger" link @click="deleteCategory(scope.row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </div>
      
      <el-dialog
        v-model="categoryDialogVisible"
        :title="editingCategory ? '编辑分类' : '添加分类'"
        width="400px"
      >
        <el-form
          ref="categoryFormRef"
          :model="categoryForm"
          :rules="categoryRules"
          label-width="80px"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="categoryForm.sort" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="categoryForm.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCategory" :loading="saving">确定</el-button>
        </template>
      </el-dialog>
    </el-main>
    <Footer />
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getPendingProducts, auditProduct } from '@/api/product'
import { getAllUsers, updateUserStatus, deleteUser } from '@/api/user'
import { getAllCategories, addCategory, updateCategory, deleteCategory as deleteCategoryApi } from '@/api/category'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const activeTab = ref('products')
const loading = ref(false)
const saving = ref(false)

const productList = ref([])
const userList = ref([])
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const categoryDialogVisible = ref(false)
const categoryFormRef = ref(null)
const editingCategory = ref(null)

const categoryForm = reactive({
  name: '',
  sort: 0,
  status: 1
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    active: 'success',
    sold: 'info',
    removed: 'danger',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    pending: '待审核',
    active: '已上架',
    sold: '已售出',
    removed: '已下架',
    rejected: '审核未通过'
  }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

const fetchPendingProducts = async () => {
  loading.value = true
  try {
    const res = await getPendingProducts({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    productList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    userList.value = res.data || []
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getAllCategories()
    categoryList.value = res.data || []
  } catch (e) {
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

const loadData = () => {
  switch (activeTab.value) {
    case 'products':
      fetchPendingProducts()
      break
    case 'users':
      fetchUsers()
      break
    case 'categories':
      fetchCategories()
      break
  }
}

onMounted(() => {
  loadData()
})

watch(activeTab, () => {
  loadData()
})

const viewProduct = (id) => {
  router.push(`/product/${id}`)
}

const auditProduct = async (row, status) => {
  const actionText = status === 'active' ? '通过' : '驳回'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该商品的审核吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: status === 'active' ? 'success' : 'warning'
    })
    
    await auditProduct(row.id, {
      status,
      remark: status === 'active' ? '审核通过' : '审核未通过'
    })
    ElMessage.success(`审核${actionText}成功`)
    fetchPendingProducts()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('操作失败', e)
    }
  }
}

const takeDownProduct = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('功能开发中')
  } catch (e) {
    if (e !== 'cancel') {
      console.error('操作失败', e)
    }
  }
}

const toggleUserStatus = async (row) => {
  const actionText = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateUserStatus(row.id, {
      status: row.status === 1 ? 0 : 1
    })
    ElMessage.success(`用户已${actionText}`)
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('操作失败', e)
    }
  }
}

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    await deleteUser(row.id)
    ElMessage.success('用户已删除')
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败', e)
    }
  }
}

const openCategoryDialog = (category = null) => {
  editingCategory.value = category
  if (category) {
    categoryForm.name = category.name
    categoryForm.sort = category.sort
    categoryForm.status = category.status
  } else {
    categoryForm.name = ''
    categoryForm.sort = 0
    categoryForm.status = 1
  }
  categoryDialogVisible.value = true
}

const saveCategory = async () => {
  if (!categoryFormRef.value) return
  
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (editingCategory.value) {
          await updateCategory(editingCategory.value.id, categoryForm)
          ElMessage.success('分类更新成功')
        } else {
          await addCategory(categoryForm)
          ElMessage.success('分类添加成功')
        }
        categoryDialogVisible.value = false
        fetchCategories()
      } catch (e) {
        console.error('保存失败', e)
      } finally {
        saving.value = false
      }
    }
  })
}

const deleteCategory = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCategoryApi(row.id)
    ElMessage.success('分类已删除')
    fetchCategories()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败', e)
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchPendingProducts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchPendingProducts()
}
</script>

<style scoped>
.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding-top: 80px;
  padding-bottom: 40px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.table-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.title-cell {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.title {
  color: #303133;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price {
  color: #f56c6c;
  font-size: 14px;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
