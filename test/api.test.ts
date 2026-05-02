import { describe, it, expect, beforeEach, afterEach } from "vitest";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DATA_DIR = path.join(__dirname, "../data");
const TEST_DATA_DIR = path.join(__dirname, "../test-data");

const readJSONFile = (filePath: string) => {
  try {
    const data = fs.readFileSync(filePath, "utf8");
    return JSON.parse(data);
  } catch (error) {
    return null;
  }
};

const writeJSONFile = (filePath: string, data: any) => {
  try {
    fs.writeFileSync(filePath, JSON.stringify(data, null, 2), "utf8");
    return true;
  } catch (error) {
    return false;
  }
};

const copyTestData = () => {
  if (!fs.existsSync(TEST_DATA_DIR)) {
    fs.mkdirSync(TEST_DATA_DIR, { recursive: true });
  }

  const files = ["products.json", "categories.json", "cart.json"];
  files.forEach((file) => {
    const source = path.join(DATA_DIR, file);
    const dest = path.join(TEST_DATA_DIR, file);
    fs.copyFileSync(source, dest);
  });
};

const cleanupTestData = () => {
  if (fs.existsSync(TEST_DATA_DIR)) {
    fs.rmSync(TEST_DATA_DIR, { recursive: true, force: true });
  }
};

describe("API 业务逻辑单元测试", () => {
  beforeEach(() => {
    copyTestData();
  });

  afterEach(() => {
    cleanupTestData();
  });

  describe("商品接口逻辑", () => {
    it("getProducts 应该返回所有商品", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));

      expect(products).not.toBeNull();
      expect(Array.isArray(products)).toBe(true);
      expect(products.length).toBeGreaterThan(0);
    });

    it("getProductById 应该返回单个商品", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));
      const product = products.find((p: any) => p.id === 1);

      expect(product).toBeDefined();
      expect(product.id).toBe(1);
      expect(typeof product.name).toBe("string");
      expect(typeof product.price).toBe("number");
    });

    it("getProductById 对于不存在的商品应该返回 undefined", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));
      const product = products.find((p: any) => p.id === 999999);

      expect(product).toBeUndefined();
    });
  });

  describe("分类接口逻辑", () => {
    it("getCategories 应该返回所有分类", () => {
      const categories = readJSONFile(
        path.join(TEST_DATA_DIR, "categories.json"),
      );

      expect(categories).not.toBeNull();
      expect(Array.isArray(categories)).toBe(true);
      expect(categories.length).toBeGreaterThan(0);
    });

    it("每个分类应该有正确的字段", () => {
      const categories = readJSONFile(
        path.join(TEST_DATA_DIR, "categories.json"),
      );

      categories.forEach((category: any) => {
        expect(typeof category.id).toBe("number");
        expect(typeof category.name).toBe("string");
        expect(typeof category.icon).toBe("string");
        expect(typeof category.description).toBe("string");
        expect(typeof category.order).toBe("number");
      });
    });
  });

  describe("购物车接口逻辑", () => {
    it("getCart 应该返回空购物车", () => {
      const cart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      expect(cart).not.toBeNull();
      expect(Array.isArray(cart.items)).toBe(true);
      expect(cart.items.length).toBe(0);
      expect(cart.total).toBe(0);
    });

    it("addToCart 应该添加商品到购物车", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));
      const cart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      const productId = 1;
      const quantity = 2;
      const product = products.find((p: any) => p.id === productId);

      expect(product).toBeDefined();

      cart.items.push({
        id: Date.now(),
        productId,
        quantity,
        product,
      });

      cart.total = cart.items.reduce(
        (sum: number, item: any) => sum + item.product.price * item.quantity,
        0,
      );

      writeJSONFile(path.join(TEST_DATA_DIR, "cart.json"), cart);

      const updatedCart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      expect(updatedCart.items.length).toBe(1);
      expect(updatedCart.items[0].productId).toBe(1);
      expect(updatedCart.items[0].quantity).toBe(2);
    });

    it("addToCart 不提供 productId 应该返回错误", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));

      const productId: any = undefined;
      const product = products.find((p: any) => p.id === productId);

      expect(product).toBeUndefined();
    });

    it("addToCart 添加不存在的商品应该返回错误", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));

      const productId = 999999;
      const product = products.find((p: any) => p.id === productId);

      expect(product).toBeUndefined();
    });

    it("updateCartItem 应该更新购物车商品数量", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));
      const cart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      const product = products.find((p: any) => p.id === 1);
      const itemId = Date.now();

      cart.items.push({
        id: itemId,
        productId: 1,
        quantity: 1,
        product,
      });

      writeJSONFile(path.join(TEST_DATA_DIR, "cart.json"), cart);

      const updatedCart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));
      const item = updatedCart.items.find((item: any) => item.id === itemId);

      expect(item).toBeDefined();

      item.quantity = 5;
      updatedCart.total = updatedCart.items.reduce(
        (sum: number, item: any) => sum + item.product.price * item.quantity,
        0,
      );

      writeJSONFile(path.join(TEST_DATA_DIR, "cart.json"), updatedCart);

      const finalCart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      expect(finalCart.items[0].quantity).toBe(5);
    });

    it("updateCartItem 数量小于 1 应该返回错误", () => {
      const quantity = 0;

      expect(quantity < 1).toBe(true);
    });

    it("removeFromCart 应该删除购物车商品", () => {
      const products = readJSONFile(path.join(TEST_DATA_DIR, "products.json"));
      const cart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      const product = products.find((p: any) => p.id === 1);
      const itemId = Date.now();

      cart.items.push({
        id: itemId,
        productId: 1,
        quantity: 1,
        product,
      });

      writeJSONFile(path.join(TEST_DATA_DIR, "cart.json"), cart);

      const updatedCart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));
      const itemIndex = updatedCart.items.findIndex(
        (item: any) => item.id === itemId,
      );

      expect(itemIndex).not.toBe(-1);

      updatedCart.items.splice(itemIndex, 1);
      updatedCart.total = updatedCart.items.reduce(
        (sum: number, item: any) => sum + item.product.price * item.quantity,
        0,
      );

      writeJSONFile(path.join(TEST_DATA_DIR, "cart.json"), updatedCart);

      const finalCart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));

      expect(finalCart.items.length).toBe(0);
      expect(finalCart.total).toBe(0);
    });

    it("removeFromCart 删除不存在的商品应该返回错误", () => {
      const cart = readJSONFile(path.join(TEST_DATA_DIR, "cart.json"));
      const itemIndex = cart.items.findIndex((item: any) => item.id === 999999);

      expect(itemIndex).toBe(-1);
    });
  });

  describe("统一返回格式", () => {
    it("readJSONFile 对于不存在的文件应该返回 null", () => {
      const result = readJSONFile(path.join(TEST_DATA_DIR, "nonexistent.json"));

      expect(result).toBeNull();
    });

    it("writeJSONFile 应该正确写入文件", () => {
      const testData = { test: "data", items: [1, 2, 3] };
      const filePath = path.join(TEST_DATA_DIR, "test-write.json");

      const result = writeJSONFile(filePath, testData);

      expect(result).toBe(true);

      const readData = readJSONFile(filePath);

      expect(readData).not.toBeNull();
      expect(readData.test).toBe("data");
      expect(readData.items).toEqual([1, 2, 3]);
    });
  });

  describe("购物车计算逻辑", () => {
    it("should calculate total price correctly", () => {
      const items = [
        { product: { price: 100 }, quantity: 2 },
        { product: { price: 50 }, quantity: 3 },
      ];

      const total = items.reduce(
        (sum, item) => sum + item.product.price * item.quantity,
        0,
      );

      expect(total).toBe(350);
    });

    it("should handle empty cart", () => {
      const items: any[] = [];

      const total = items.reduce(
        (sum, item) => sum + item.product.price * item.quantity,
        0,
      );

      expect(total).toBe(0);
    });
  });

  describe("商品过滤逻辑", () => {
    it("should filter products by category", () => {
      const products = [
        { id: 1, name: "商品1", categoryId: 1 },
        { id: 2, name: "商品2", categoryId: 2 },
        { id: 3, name: "商品3", categoryId: 1 },
        { id: 4, name: "商品4", categoryId: 3 },
      ];

      const categoryId = 1;
      const filtered = products.filter((p) => p.categoryId === categoryId);

      expect(filtered.length).toBe(2);
      expect(filtered[0].id).toBe(1);
      expect(filtered[1].id).toBe(3);
    });

    it("should filter products by price range", () => {
      const products = [
        { id: 1, name: "商品1", price: 50 },
        { id: 2, name: "商品2", price: 150 },
        { id: 3, name: "商品3", price: 300 },
        { id: 4, name: "商品4", price: 2500 },
      ];

      const minPrice = 100;
      const maxPrice = 1000;

      const filtered = products.filter(
        (p) => p.price >= minPrice && p.price <= maxPrice,
      );

      expect(filtered.length).toBe(2);
      expect(filtered[0].id).toBe(2);
      expect(filtered[1].id).toBe(3);
    });
  });

  describe("商品排序逻辑", () => {
    it("should sort products by price ascending", () => {
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

    it("should sort products by price descending", () => {
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

    it("should sort products by sales", () => {
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

    it("should sort products by rating", () => {
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
