import { describe, it, expect, beforeEach } from "vitest";
import { ref, computed } from "vue";

describe("购物车状态管理逻辑测试", () => {
  describe("计算属性测试", () => {
    it("cartItems 应该返回购物车商品列表", () => {
      const cart = ref({
        items: [
          {
            id: 1,
            productId: 1,
            quantity: 2,
            product: { id: 1, name: "商品1", price: 100, image: "" },
          },
          {
            id: 2,
            productId: 2,
            quantity: 1,
            product: { id: 2, name: "商品2", price: 200, image: "" },
          },
        ],
        total: 400,
      });

      const cartItems = computed(() => {
        return cart.value.items;
      });

      expect(cartItems.value.length).toBe(2);
      expect(cartItems.value[0].productId).toBe(1);
      expect(cartItems.value[1].productId).toBe(2);
    });

    it("formattedTotal 应该返回格式化后的总价", () => {
      const cart = ref({
        items: [],
        total: 123.456,
      });

      const formattedTotal = computed(() => {
        return cart.value.total.toFixed(2);
      });

      expect(formattedTotal.value).toBe("123.46");
    });

    it("isFreeShipping 应该判断是否免运费", () => {
      const cart = ref({
        items: [],
        total: 98,
      });

      const isFreeShipping = computed(() => {
        return cart.value.total >= 99;
      });

      expect(isFreeShipping.value).toBe(false);

      cart.value.total = 99;
      expect(isFreeShipping.value).toBe(true);

      cart.value.total = 100;
      expect(isFreeShipping.value).toBe(true);
    });

    it("shippingText 应该返回运费文本", () => {
      const cart = ref({
        items: [],
        total: 50,
      });

      const isFreeShipping = computed(() => {
        return cart.value.total >= 99;
      });

      const shippingText = computed(() => {
        return isFreeShipping.value ? "免运费" : "¥10.00";
      });

      expect(shippingText.value).toBe("¥10.00");

      cart.value.total = 100;
      expect(shippingText.value).toBe("免运费");
    });

    it("grandTotal 应该计算应付总额", () => {
      const cart = ref({
        items: [],
        total: 50,
      });

      const isFreeShipping = computed(() => {
        return cart.value.total >= 99;
      });

      const grandTotal = computed(() => {
        return cart.value.total + (isFreeShipping.value ? 0 : 10);
      });

      expect(grandTotal.value).toBe(60);

      cart.value.total = 100;
      expect(grandTotal.value).toBe(100);
    });

    it("remainingForFreeShipping 应该计算免运费还差多少", () => {
      const cart = ref({
        items: [],
        total: 50,
      });

      const remainingForFreeShipping = computed(() => {
        return Math.max(0, 99 - cart.value.total);
      });

      expect(remainingForFreeShipping.value).toBe(49);

      cart.value.total = 99;
      expect(remainingForFreeShipping.value).toBe(0);

      cart.value.total = 100;
      expect(remainingForFreeShipping.value).toBe(0);
    });
  });

  describe("工具函数测试", () => {
    it("getItemSubtotal 应该计算商品小计", () => {
      const getItemSubtotal = (item: any) => {
        return (item.product.price * item.quantity).toFixed(2);
      };

      const item = {
        id: 1,
        productId: 1,
        quantity: 2,
        product: { id: 1, name: "商品1", price: 99.99, image: "" },
      };

      expect(getItemSubtotal(item)).toBe("199.98");
    });

    it("产品链接应该正确拼接", () => {
      const productId = 123;
      const link = "/products/" + productId;

      expect(link).toBe("/products/123");
    });
  });

  describe("数据过滤测试", () => {
    it("应该能够按分类过滤商品", () => {
      const allProducts = [
        { id: 1, name: "商品1", categoryId: 1 },
        { id: 2, name: "商品2", categoryId: 2 },
        { id: 3, name: "商品3", categoryId: 1 },
        { id: 4, name: "商品4", categoryId: 3 },
      ];

      const selectedCategory = ref<number | null>(1);

      const filteredProducts = computed(() => {
        let result = [...allProducts];

        if (selectedCategory.value !== null) {
          result = result.filter(
            (p) => p.categoryId === selectedCategory.value,
          );
        }

        return result;
      });

      expect(filteredProducts.value.length).toBe(2);
      expect(filteredProducts.value[0].id).toBe(1);
      expect(filteredProducts.value[1].id).toBe(3);

      selectedCategory.value = null;
      expect(filteredProducts.value.length).toBe(4);
    });

    it("应该能够按价格范围过滤商品", () => {
      const allProducts = [
        { id: 1, name: "商品1", price: 50 },
        { id: 2, name: "商品2", price: 150 },
        { id: 3, name: "商品3", price: 300 },
        { id: 4, name: "商品4", price: 2500 },
      ];

      const priceRange = ref<{ min: number; max: number | null } | null>({
        min: 100,
        max: 1000,
      });

      const filteredProducts = computed(() => {
        let result = [...allProducts];

        if (priceRange.value) {
          result = result.filter((p) => {
            if (priceRange.value?.max === null) {
              return p.price >= priceRange.value.min;
            }
            return (
              p.price >= priceRange.value.min && p.price <= priceRange.value.max
            );
          });
        }

        return result;
      });

      expect(filteredProducts.value.length).toBe(2);
      expect(filteredProducts.value[0].id).toBe(2);
      expect(filteredProducts.value[1].id).toBe(3);

      priceRange.value = { min: 1000, max: null };
      expect(filteredProducts.value.length).toBe(1);
      expect(filteredProducts.value[0].id).toBe(4);
    });
  });

  describe("排序测试", () => {
    it("应该能够按价格升序排序", () => {
      const products = [
        { id: 1, name: "商品1", price: 300 },
        { id: 2, name: "商品2", price: 100 },
        { id: 3, name: "商品3", price: 200 },
      ];

      const sorted = [...products].sort((a, b) => a.price - b.price);

      expect(sorted[0].id).toBe(2);
      expect(sorted[1].id).toBe(3);
      expect(sorted[2].id).toBe(1);
    });

    it("应该能够按价格降序排序", () => {
      const products = [
        { id: 1, name: "商品1", price: 300 },
        { id: 2, name: "商品2", price: 100 },
        { id: 3, name: "商品3", price: 200 },
      ];

      const sorted = [...products].sort((a, b) => b.price - a.price);

      expect(sorted[0].id).toBe(1);
      expect(sorted[1].id).toBe(3);
      expect(sorted[2].id).toBe(2);
    });

    it("应该能够按销量排序", () => {
      const products = [
        { id: 1, name: "商品1", sales: 100 },
        { id: 2, name: "商品2", sales: 500 },
        { id: 3, name: "商品3", sales: 200 },
      ];

      const sorted = [...products].sort((a, b) => b.sales - a.sales);

      expect(sorted[0].id).toBe(2);
      expect(sorted[1].id).toBe(3);
      expect(sorted[2].id).toBe(1);
    });

    it("应该能够按评分排序", () => {
      const products = [
        { id: 1, name: "商品1", rating: 4.5 },
        { id: 2, name: "商品2", rating: 4.9 },
        { id: 3, name: "商品3", rating: 4.7 },
      ];

      const sorted = [...products].sort((a, b) => b.rating - a.rating);

      expect(sorted[0].id).toBe(2);
      expect(sorted[1].id).toBe(3);
      expect(sorted[2].id).toBe(1);
    });
  });
});
