const tools = {
  formatDate: (dateStr, format = 'YYYY-MM-DD') => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  },
  
  formatTime: (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  },
  
  isPhone: (phone) => {
    return /^1[3-9]\d{9}$/.test(phone)
  },
  
  isEmail: (email) => {
    return /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(email)
  },
  
  storage: {
    set: (key, value) => {
      if (typeof value === 'object') {
        localStorage.setItem(key, JSON.stringify(value))
      } else {
        localStorage.setItem(key, value)
      }
    },
    get: (key) => {
      const value = localStorage.getItem(key)
      try {
        return JSON.parse(value)
      } catch {
        return value
      }
    },
    remove: (key) => {
      localStorage.removeItem(key)
    },
    clear: () => {
      localStorage.clear()
    }
  },
  
  debounce: (fn, delay = 300) => {
    let timer = null
    return (...args) => {
      clearTimeout(timer)
      timer = setTimeout(() => {
        fn.apply(this, args)
      }, delay)
    }
  },
  
  throttle: (fn, delay = 300) => {
    let lastTime = 0
    return (...args) => {
      const now = Date.now()
      if (now - lastTime >= delay) {
        lastTime = now
        fn.apply(this, args)
      }
    }
  },
  
  getMemberId: () => {
    return localStorage.getItem('memberId') || null
  },
  
  getMember: () => {
    const member = localStorage.getItem('member')
    return member ? JSON.parse(member) : null
  },
  
  setMember: (member) => {
    localStorage.setItem('member', JSON.stringify(member))
    localStorage.setItem('memberId', member.id)
  },
  
  clearMember: () => {
    localStorage.removeItem('member')
    localStorage.removeItem('memberId')
  }
}

export default tools
export { tools }
