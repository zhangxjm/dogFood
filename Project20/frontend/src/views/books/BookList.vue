<template>
  <div class="book-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书列表</span>
          <el-button type="primary" @click="handleBorrow" :disabled="selectedBooks.length !== 1">
            借书
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input
            v-model="searchForm.search"
            placeholder="请输入书名/作者/ISBN"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="可借" value="available" />
            <el-option label="已借" value="borrowed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table
        :data="bookList"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="title" label="书名" min-width="200" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="category_name" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="available_quantity" label="库存" width="80" />
        <el-table-column prop="status_display" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'available' ? 'success' : 'warning'">
              {{ scope.row.status_display }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              :disabled="!scope.row.is_available"
              @click="handleBorrowOne(scope.row)"
            >
              借书
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <el-dialog
      v-model="borrowDialogVisible"
      title="确认借书"
      width="400px"
    >
      <el-form :model="borrowForm" label-width="80px">
        <el-form-item label="图书">
          <el-input :value="currentBook?.title" disabled />
        </el-form-item>
        <el-form-item label="作者">
          <el-input :value="currentBook?.author" disabled />
        </el-form-item>
        <el-form-item label="借阅天数">
          <el-input-number
            v-model="borrowForm.borrow_days"
            :min="1"
            :max="90"
            :step="1"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBorrow" :loading="borrowLoading">
          确认借书
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableBooks, getActiveCategories } from '@/api/book'
import { borrowBook } from '@/api/borrow'

const loading = ref(false)
const borrowLoading = ref(false)
const borrowDialogVisible = ref(false)
const currentBook = ref(null)
const selectedBooks = ref([])
const bookList = ref([])
const categories = ref([])

const searchForm = reactive({
  search: '',
  category: '',
  status: '',
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const borrowForm = reactive({
  borrow_days: 30,
})

const fetchCategories = async () => {
  try {
    const res = await getActiveCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchBooks = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.pageSize,
      search: searchForm.search || undefined,
      category: searchForm.category || undefined,
      status: searchForm.status || undefined,
    }
    
    const res = await getAvailableBooks(params)
    
    if (res.data?.results) {
      bookList.value = res.data.results
      pagination.total = res.data.count
    } else {
      bookList.value = res.data || []
      pagination.total = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchBooks()
}

const resetSearch = () => {
  searchForm.search = ''
  searchForm.category = ''
  searchForm.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchBooks()
}

const handleSelectionChange = (val) => {
  selectedBooks.value = val
}

const handleBorrowOne = (row) => {
  currentBook.value = row
  borrowForm.borrow_days = 30
  borrowDialogVisible.value = true
}

const handleBorrow = () => {
  if (selectedBooks.value.length === 1) {
    handleBorrowOne(selectedBooks.value[0])
  }
}

const confirmBorrow = async () => {
  if (!currentBook.value) return
  
  borrowLoading.value = true
  try {
    await borrowBook({
      book_id: currentBook.value.id,
      borrow_days: borrowForm.borrow_days,
    })
    
    ElMessage.success('借书成功')
    borrowDialogVisible.value = false
    fetchBooks()
  } catch (error) {
    console.error('借书失败:', error)
  } finally {
    borrowLoading.value = false
  }
}

onMounted(() => {
  fetchCategories()
  fetchBooks()
})
</script>

<style scoped>
.book-list-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
