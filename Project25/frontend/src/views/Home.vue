<template>
  <div class="home">
    <el-carousel :interval="4000" height="300px" class="banner">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{ background: item.bg }">
          <div class="banner-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" @click="$router.push('/movies')">立即购票</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="content-wrapper">
      <el-tabs v-model="activeTab" class="movie-tabs">
        <el-tab-pane label="正在热映" name="showing">
          <div class="movie-grid">
            <el-card
              v-for="movie in showingMovies"
              :key="movie.id"
              class="movie-card"
              @click="$router.push(`/movies/${movie.id}`)"
              :body-style="{ padding: 0 }"
            >
              <div class="movie-poster">
                <img :src="movie.poster" :alt="movie.title" />
                <div class="movie-rating" v-if="movie.rating > 0">
                  <el-rate :model-value="movie.rating / 2" disabled />
                  <span>{{ movie.rating }}</span>
                </div>
              </div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="movie-desc">{{ movie.duration }}分钟 | {{ movie.category_name || '未分类' }}</p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="即将上映" name="coming">
          <div class="movie-grid">
            <el-card
              v-for="movie in comingMovies"
              :key="movie.id"
              class="movie-card"
              @click="$router.push(`/movies/${movie.id}`)"
              :body-style="{ padding: 0 }"
            >
              <div class="movie-poster">
                <img :src="movie.poster" :alt="movie.title" />
                <div class="coming-badge">即将上映</div>
              </div>
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="movie-desc">
                  {{ movie.release_date ? new Date(movie.release_date).toLocaleDateString('zh-CN') + ' 上映' : '待定' }}
                </p>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="hot-section" v-if="hotMovies.length > 0">
        <h2 class="section-title">
          <el-icon><Fire /></el-icon>
          热门推荐
        </h2>
        <div class="hot-movies">
          <el-card
            v-for="movie in hotMovies"
            :key="movie.id"
            class="hot-card"
            @click="$router.push(`/movies/${movie.id}`)"
            :body-style="{ padding: '12px' }"
          >
            <el-row :gutter="12">
              <el-col :span="8">
                <img :src="movie.poster" :alt="movie.title" class="hot-poster" />
              </el-col>
              <el-col :span="16">
                <h3>{{ movie.title }}</h3>
                <p class="hot-meta">
                  <span>{{ movie.duration }}分钟</span>
                  <span class="divider">|</span>
                  <span>{{ movie.category_name }}</span>
                </p>
                <div class="hot-rating">
                  <el-rate :model-value="movie.rating / 2" disabled />
                  <span>{{ movie.rating }}分</span>
                </div>
                <p class="hot-desc">{{ movie.description?.slice(0, 80) }}...</p>
                <el-button type="primary" size="small" @click.stop>
                  立即购票
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../utils/request'

const activeTab = ref('showing')
const showingMovies = ref([])
const comingMovies = ref([])
const hotMovies = ref([])

const banners = ref([
  {
    id: 1,
    title: '流浪地球3 震撼上映',
    description: '太阳危机，地球流浪，人类命运何去何从',
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 2,
    title: '热辣滚烫 笑中带泪',
    description: '贾玲导演倾情奉献，感动与欢笑并存',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 3,
    title: '飞驰人生2 热血回归',
    description: '沈腾演绎赛车梦想，速度与激情',
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
])

const fetchMovies = async () => {
  try {
    const [showingRes, comingRes, hotRes] = await Promise.all([
      api.get('/movies/showing'),
      api.get('/movies/coming'),
      api.get('/movies/hot')
    ])
    showingMovies.value = showingRes.data
    comingMovies.value = comingRes.data
    hotMovies.value = hotRes.data
  } catch (error) {
    console.error('Failed to fetch movies:', error)
  }
}

onMounted(() => {
  fetchMovies()
})
</script>

<style scoped>
.home {
  min-height: 100%;
}

.banner {
  margin-bottom: 30px;
}

.banner-item {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-content {
  text-align: center;
  color: #fff;
  z-index: 10;
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.movie-tabs {
  margin-bottom: 40px;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
  padding: 10px 0;
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
  height: 240px;
  overflow: hidden;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-rating {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: #ffd700;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.coming-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #f56c6c;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.movie-info {
  padding: 12px;
}

.movie-title {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-desc {
  font-size: 12px;
  color: #999;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hot-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}

.section-title .el-icon {
  color: #f56c6c;
}

.hot-movies {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.hot-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.hot-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.hot-poster {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
}

.hot-card h3 {
  font-size: 16px;
  margin: 0 0 8px 0;
}

.hot-meta {
  font-size: 13px;
  color: #999;
  margin: 0 0 8px 0;
}

.hot-meta .divider {
  margin: 0 8px;
}

.hot-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.hot-rating span {
  color: #ffd700;
  font-weight: bold;
}

.hot-desc {
  font-size: 13px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.5;
}
</style>
