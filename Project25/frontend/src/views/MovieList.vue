<template>
  <div class="movie-list">
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索电影"
        style="width: 200px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="selectedCategory" placeholder="选择分类" @change="fetchMovies">
        <el-option label="全部" value="" />
        <el-option
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
      <el-select v-model="selectedStatus" placeholder="选择状态" @change="fetchMovies">
        <el-option label="全部" value="" />
        <el-option label="正在热映" value="showing" />
        <el-option label="即将上映" value="coming" />
      </el-select>
    </div>

    <div class="movie-grid">
      <el-card
        v-for="movie in movies"
        :key="movie.id"
        class="movie-card"
        @click="$router.push(`/movies/${movie.id}`)"
        :body-style="{ padding: 0 }"
      >
        <div class="movie-poster">
          <img :src="movie.poster" :alt="movie.title" />
          <div class="movie-badge" v-if="movie.is_hot">
            <el-icon><Fire /></el-icon>
            热门
          </div>
          <div class="movie-status" v-if="movie.status === 'coming'">
            即将上映
          </div>
        </div>
        <div class="movie-info">
          <h3 class="movie-title">{{ movie.title }}</h3>
          <p class="movie-meta">
            <span>{{ movie.duration }}分钟</span>
            <span class="divider">|</span>
            <span>{{ movie.category_name || '未分类' }}</span>
          </p>
          <div class="movie-rating" v-if="movie.rating > 0">
            <el-rate :model-value="movie.rating / 2" disabled />
            <span>{{ movie.rating }}分</span>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-if="movies.length === 0 && !loading" description="暂无电影" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedStatus = ref('')
const movies = ref([])
const categories = ref([])
const loading = ref(false)

const fetchCategories = async () => {
  try {
    const response = await api.get('/movies/categories')
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const fetchMovies = async () => {
  loading.value = true
  try {
    const params = {}
    if (selectedCategory.value) {
      params.category_id = selectedCategory.value
    }
    if (selectedStatus.value) {
      params.status = selectedStatus.value
    }
    if (searchKeyword.value) {
      params.search = searchKeyword.value
    }
    
    const response = await api.get('/movies', { params })
    movies.value = response.data
  } catch (error) {
    console.error('Failed to fetch movies:', error)
    ElMessage.error('获取电影列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchMovies()
}

onMounted(() => {
  fetchCategories()
  fetchMovies()
})
</script>

<style scoped>
.movie-list {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.movie-poster {
  position: relative;
  height: 280px;
  overflow: hidden;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff6b6b, #f56c6c);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.movie-status {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #67c23a;
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.movie-info {
  padding: 16px;
}

.movie-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-meta {
  font-size: 13px;
  color: #999;
  margin: 0 0 8px 0;
}

.movie-meta .divider {
  margin: 0 8px;
}

.movie-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.movie-rating span {
  color: #ffd700;
  font-weight: bold;
  font-size: 14px;
}
</style>
