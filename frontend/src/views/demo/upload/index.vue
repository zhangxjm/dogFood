<template>
  <div class="upload-demo">
    <el-card>
      <template #header>
        <span>上传组件演示</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>基础文件上传</h3>
          <el-upload
            class="upload-demo1"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                只能上传 jpg/png 文件，且不超过 500kb
              </div>
            </template>
          </el-upload>
          <el-button type="primary" :disabled="fileList.length === 0" style="margin-top: 15px">
            上传文件
          </el-button>
        </el-col>

        <el-col :span="12">
          <h3>图片上传预览</h3>
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            accept="image/*"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传头像</div>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>多文件上传</h3>
          <el-upload
            class="upload-demo2"
            action="#"
            :auto-upload="false"
            multiple
            :limit="3"
            :on-exceed="handleExceed"
            :file-list="multiFileList"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                最多上传 3 个文件
              </div>
            </template>
          </el-upload>
          <el-button type="primary" :disabled="multiFileList.length === 0" style="margin-top: 15px">
            开始上传
          </el-button>
        </el-col>

        <el-col :span="12">
          <h3>图片列表上传</h3>
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :file-list="pictureFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-dialog v-model="dialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" />
          </el-dialog>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <h3>手动控制上传</h3>
          <el-upload
            ref="uploadRef"
            class="upload-demo3"
            action="#"
            :auto-upload="false"
            :file-list="manualFileList"
          >
            <el-button type="primary">选择文件</el-button>
            <el-button type="success" @click="submitUpload">上传到服务器</el-button>
            <template #tip>
              <div class="el-upload__tip">
                点击"上传到服务器"按钮提交
              </div>
            </template>
          </el-upload>
        </el-col>

        <el-col :span="12">
          <h3>拖拽排序</h3>
          <el-upload
            action="#"
            drag
            :auto-upload="false"
            :file-list="sortFileList"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
          </el-upload>
          <div class="upload-tip" style="margin-top: 10px">支持拖拽上传和排序</div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, type UploadFile, type UploadInstance, type UploadFiles } from 'element-plus'
import { ElMessage } from 'element-plus'

const fileList = ref<UploadFile[]>([])
const multiFileList = ref<UploadFile[]>([])
const pictureFileList = ref<UploadFile[]>([])
const manualFileList = ref<UploadFile[]>([])
const sortFileList = ref<UploadFile[]>([])
const imageUrl = ref('')
const dialogVisible = ref(false)
const dialogImageUrl = ref('')
const uploadRef = ref<UploadInstance>()

const handleFileChange = (file: UploadFile, files: UploadFile[]) => {
  fileList.value = files
  console.log('文件变更:', file, files)
}

const handleAvatarChange = (file: UploadFile) => {
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      imageUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(file.raw)
  }
}

const handleExceed = (files: File[], uploadFiles: UploadFile[]) => {
  ElMessage.warning(`最多只能上传 3 个文件，当前已选择 ${uploadFiles.length} 个`)
}

const handlePictureCardPreview = (uploadFile: UploadFile) => {
  dialogImageUrl.value = uploadFile.url!
  dialogVisible.value = true
}

const handleRemove = (uploadFile: UploadFile, uploadFiles: UploadFile[]) => {
  console.log('删除文件:', uploadFile, uploadFiles)
}

const submitUpload = () => {
  uploadRef.value?.submit()
  ElMessage.success('上传成功')
}
</script>

<style scoped>
.upload-demo h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
}

.upload-tip {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
