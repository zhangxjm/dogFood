interface CartItem {
  id: number
  productId: number
  quantity: number
  product: {
    id: number
    name: string
    price: number
    image: string
  }
}

interface Cart {
  items: CartItem[]
  total: number
}

export const useCart = () => {
  const config = useRuntimeConfig()
  const cart = useState<Cart>('cart', () => ({
    items: [],
    total: 0
  }))
  
  const cartCount = computed(() => 
    cart.value.items.reduce((sum, item) => sum + item.quantity, 0)
  )

  const fetchCart = async () => {
    try {
      const response = await $fetch(`${config.public.apiBase}/cart`)
      if (response.code === 200) {
        cart.value = response.data
      }
    } catch (error) {
      console.error('获取购物车失败:', error)
    }
  }

  const addToCart = async (productId: number, quantity: number = 1) => {
    try {
      const response = await $fetch(`${config.public.apiBase}/cart`, {
        method: 'POST',
        body: { productId, quantity }
      })
      
      if (response.code === 200) {
        cart.value = response.data
        return { success: true, message: '添加购物车成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('添加购物车失败:', error)
      return { success: false, message: '添加购物车失败' }
    }
  }

  const removeFromCart = async (itemId: number) => {
    try {
      const response = await $fetch(`${config.public.apiBase}/cart/${itemId}`, {
        method: 'DELETE'
      })
      
      if (response.code === 200) {
        cart.value = response.data
        return { success: true, message: '删除成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('删除购物车商品失败:', error)
      return { success: false, message: '删除失败' }
    }
  }

  const updateQuantity = async (itemId: number, quantity: number) => {
    try {
      const response = await $fetch(`${config.public.apiBase}/cart/${itemId}`, {
        method: 'PUT',
        body: { quantity }
      })
      
      if (response.code === 200) {
        cart.value = response.data
        return { success: true, message: '更新成功' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      console.error('更新购物车失败:', error)
      return { success: false, message: '更新失败' }
    }
  }

  return {
    cart,
    cartCount,
    fetchCart,
    addToCart,
    removeFromCart,
    updateQuantity
  }
}
