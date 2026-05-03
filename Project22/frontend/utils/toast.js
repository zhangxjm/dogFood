import { showToast, showSuccessToast, showFailToast, showLoadingToast, showConfirmDialog, closeToast } from 'vant'
import 'vant/es/toast/style'
import 'vant/es/dialog/style'

const toast = {
  success: (message = '操作成功') => {
    showSuccessToast({
      message,
      duration: 2000
    })
  },
  error: (message = '操作失败') => {
    showFailToast({
      message,
      duration: 2000
    })
  },
  info: (message) => {
    showToast({
      message,
      duration: 2000
    })
  },
  loading: (message = '加载中...') => {
    showLoadingToast({
      message,
      forbidClick: true,
      loadingType: 'spinner'
    })
  },
  close: () => {
    closeToast()
  },
  confirm: (message, title = '提示') => {
    return new Promise((resolve, reject) => {
      showConfirmDialog({
        title,
        message
      }).then(() => {
        resolve(true)
      }).catch(() => {
        resolve(false)
      })
    })
  }
}

export default toast
export { toast }
