import { defineStore } from "pinia";

export const useCartStore = defineStore("cart", {
  state: () => ({
    items: JSON.parse(localStorage.getItem("cartItems") || "[]"),
  }),

  getters: {
    totalCount: (state) =>
      state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalPrice: (state) =>
      state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
  },

  actions: {
    addItem(product) {
      const existingItem = this.items.find(
        (item) => item.productId === product.id,
      );
      if (existingItem) {
        existingItem.quantity += 1;
      } else {
        this.items.push({
          productId: product.id,
          productName: product.productName,
          price: product.price,
          mainImage: product.mainImage,
          quantity: 1,
        });
      }
      this.saveToLocalStorage();
    },

    removeItem(productId) {
      const index = this.items.findIndex(
        (item) => item.productId === productId,
      );
      if (index > -1) {
        this.items.splice(index, 1);
        this.saveToLocalStorage();
      }
    },

    updateQuantity(productId, quantity) {
      const item = this.items.find((item) => item.productId === productId);
      if (item) {
        item.quantity = Math.max(1, quantity);
        this.saveToLocalStorage();
      }
    },

    clearCart() {
      this.items = [];
      this.saveToLocalStorage();
    },

    saveToLocalStorage() {
      localStorage.setItem("cartItems", JSON.stringify(this.items));
    },
  },
});
