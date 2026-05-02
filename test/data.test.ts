import { describe, it, expect } from "vitest";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const dataDir = path.join(__dirname, "../data");

describe("JSON 数据文件结构验证", () => {
  describe("商品数据 (products.json)", () => {
    it("文件应该存在且格式正确", () => {
      const filePath = path.join(dataDir, "products.json");
      expect(fs.existsSync(filePath)).toBe(true);

      const content = fs.readFileSync(filePath, "utf8");
      const products = JSON.parse(content);

      expect(Array.isArray(products)).toBe(true);
      expect(products.length).toBeGreaterThan(0);
    });

    it("每个商品应该有正确的字段结构", () => {
      const filePath = path.join(dataDir, "products.json");
      const content = fs.readFileSync(filePath, "utf8");
      const products = JSON.parse(content);

      products.forEach((product: any) => {
        expect(typeof product.id).toBe("number");
        expect(typeof product.name).toBe("string");
        expect(product.name.length).toBeGreaterThan(0);
        expect(typeof product.price).toBe("number");
        expect(product.price).toBeGreaterThanOrEqual(0);
        expect(typeof product.categoryId).toBe("number");
        expect(typeof product.image).toBe("string");
        expect(Array.isArray(product.images)).toBe(true);
        expect(typeof product.description).toBe("string");
        expect(typeof product.stock).toBe("number");
        expect(typeof product.sales).toBe("number");
        expect(typeof product.rating).toBe("number");
        expect(typeof product.reviews).toBe("number");
        expect(typeof product.isHot).toBe("boolean");
        expect(typeof product.isNew).toBe("boolean");
        expect(typeof product.specs).toBe("object");
      });
    });

    it("商品价格应该大于等于原价或没有原价", () => {
      const filePath = path.join(dataDir, "products.json");
      const content = fs.readFileSync(filePath, "utf8");
      const products = JSON.parse(content);

      products.forEach((product: any) => {
        if (product.originalPrice) {
          expect(product.originalPrice).toBeGreaterThanOrEqual(product.price);
        }
      });
    });

    it("所有商品的 categoryId 应该引用有效的分类", () => {
      const productsPath = path.join(dataDir, "products.json");
      const categoriesPath = path.join(dataDir, "categories.json");

      const productsContent = fs.readFileSync(productsPath, "utf8");
      const categoriesContent = fs.readFileSync(categoriesPath, "utf8");

      const products = JSON.parse(productsContent);
      const categories = JSON.parse(categoriesContent);

      const categoryIds = new Set(categories.map((c: any) => c.id));

      products.forEach((product: any) => {
        expect(categoryIds.has(product.categoryId)).toBe(true);
      });
    });
  });

  describe("分类数据 (categories.json)", () => {
    it("文件应该存在且格式正确", () => {
      const filePath = path.join(dataDir, "categories.json");
      expect(fs.existsSync(filePath)).toBe(true);

      const content = fs.readFileSync(filePath, "utf8");
      const categories = JSON.parse(content);

      expect(Array.isArray(categories)).toBe(true);
      expect(categories.length).toBeGreaterThan(0);
    });

    it("每个分类应该有正确的字段结构", () => {
      const filePath = path.join(dataDir, "categories.json");
      const content = fs.readFileSync(filePath, "utf8");
      const categories = JSON.parse(content);

      categories.forEach((category: any) => {
        expect(typeof category.id).toBe("number");
        expect(typeof category.name).toBe("string");
        expect(category.name.length).toBeGreaterThan(0);
        expect(typeof category.icon).toBe("string");
        expect(typeof category.description).toBe("string");
        expect(typeof category.order).toBe("number");
      });
    });

    it("分类 ID 应该是唯一的", () => {
      const filePath = path.join(dataDir, "categories.json");
      const content = fs.readFileSync(filePath, "utf8");
      const categories = JSON.parse(content);

      const ids = categories.map((c: any) => c.id);
      const uniqueIds = new Set(ids);

      expect(uniqueIds.size).toBe(ids.length);
    });
  });

  describe("购物车数据 (cart.json)", () => {
    it("文件应该存在且格式正确", () => {
      const filePath = path.join(dataDir, "cart.json");
      expect(fs.existsSync(filePath)).toBe(true);

      const content = fs.readFileSync(filePath, "utf8");
      const cart = JSON.parse(content);

      expect(typeof cart).toBe("object");
      expect(Array.isArray(cart.items)).toBe(true);
      expect(typeof cart.total).toBe("number");
    });

    it("购物车初始状态应该为空", () => {
      const filePath = path.join(dataDir, "cart.json");
      const content = fs.readFileSync(filePath, "utf8");
      const cart = JSON.parse(content);

      expect(cart.items.length).toBe(0);
      expect(cart.total).toBe(0);
    });
  });
});
