import http from 'http';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PORT = process.env.PORT || 3001;
const DATA_DIR = path.join(__dirname, '../data');

const readJSONFile = (filePath) => {
  try {
    const data = fs.readFileSync(filePath, 'utf8');
    return JSON.parse(data);
  } catch (error) {
    console.error(`Error reading file ${filePath}:`, error);
    return null;
  }
};

const writeJSONFile = (filePath, data) => {
  try {
    fs.writeFileSync(filePath, JSON.stringify(data, null, 2), 'utf8');
    return true;
  } catch (error) {
    console.error(`Error writing file ${filePath}:`, error);
    return false;
  }
};

const sendResponse = (res, statusCode, data, message = 'success') => {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type'
  });
  
  const response = {
    code: statusCode === 200 || statusCode === 201 ? 200 : statusCode,
    message,
    data
  };
  
  res.end(JSON.stringify(response));
};

const parseRequestBody = (req) => {
  return new Promise((resolve, reject) => {
    let body = '';
    
    req.on('data', (chunk) => {
      body += chunk.toString();
    });
    
    req.on('end', () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch (error) {
        reject(error);
      }
    });
    
    req.on('error', (error) => {
      reject(error);
    });
  });
};

const getProducts = (req, res) => {
  const products = readJSONFile(path.join(DATA_DIR, 'products.json'));
  if (!products) {
    return sendResponse(res, 500, null, '读取商品数据失败');
  }
  sendResponse(res, 200, products);
};

const getProductById = (req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const id = parseInt(url.pathname.split('/').pop());
  
  const products = readJSONFile(path.join(DATA_DIR, 'products.json'));
  if (!products) {
    return sendResponse(res, 500, null, '读取商品数据失败');
  }
  
  const product = products.find(p => p.id === id);
  if (!product) {
    return sendResponse(res, 404, null, '商品不存在');
  }
  
  sendResponse(res, 200, product);
};

const getProductsByCategory = (req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const categoryId = parseInt(url.pathname.split('/').pop());
  
  const products = readJSONFile(path.join(DATA_DIR, 'products.json'));
  if (!products) {
    return sendResponse(res, 500, null, '读取商品数据失败');
  }
  
  const filteredProducts = products.filter(p => p.categoryId === categoryId);
  sendResponse(res, 200, filteredProducts);
};

const getCategories = (req, res) => {
  const categories = readJSONFile(path.join(DATA_DIR, 'categories.json'));
  if (!categories) {
    return sendResponse(res, 500, null, '读取分类数据失败');
  }
  sendResponse(res, 200, categories);
};

const getCart = (req, res) => {
  const cart = readJSONFile(path.join(DATA_DIR, 'cart.json'));
  if (!cart) {
    return sendResponse(res, 500, null, '读取购物车数据失败');
  }
  sendResponse(res, 200, cart);
};

const addToCart = async (req, res) => {
  try {
    const body = await parseRequestBody(req);
    const { productId, quantity = 1 } = body;
    
    if (!productId) {
      return sendResponse(res, 400, null, '商品ID不能为空');
    }
    
    const products = readJSONFile(path.join(DATA_DIR, 'products.json'));
    const product = products.find(p => p.id === productId);
    
    if (!product) {
      return sendResponse(res, 404, null, '商品不存在');
    }
    
    const cart = readJSONFile(path.join(DATA_DIR, 'cart.json'));
    const existingItem = cart.items.find(item => item.productId === productId);
    
    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      cart.items.push({
        id: Date.now(),
        productId,
        quantity,
        product
      });
    }
    
    cart.total = cart.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
    
    if (writeJSONFile(path.join(DATA_DIR, 'cart.json'), cart)) {
      sendResponse(res, 201, cart, '添加购物车成功');
    } else {
      sendResponse(res, 500, null, '添加购物车失败');
    }
  } catch (error) {
    sendResponse(res, 400, null, '请求参数错误');
  }
};

const removeFromCart = (req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const id = parseInt(url.pathname.split('/').pop());
  
  const cart = readJSONFile(path.join(DATA_DIR, 'cart.json'));
  const itemIndex = cart.items.findIndex(item => item.id === id);
  
  if (itemIndex === -1) {
    return sendResponse(res, 404, null, '购物车商品不存在');
  }
  
  cart.items.splice(itemIndex, 1);
  cart.total = cart.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  
  if (writeJSONFile(path.join(DATA_DIR, 'cart.json'), cart)) {
    sendResponse(res, 200, cart, '删除购物车商品成功');
  } else {
    sendResponse(res, 500, null, '删除购物车商品失败');
  }
};

const updateCartItem = async (req, res) => {
  try {
    const url = new URL(req.url, `http://localhost:${PORT}`);
    const id = parseInt(url.pathname.split('/').pop());
    const body = await parseRequestBody(req);
    const { quantity } = body;
    
    if (!quantity || quantity < 1) {
      return sendResponse(res, 400, null, '数量必须大于0');
    }
    
    const cart = readJSONFile(path.join(DATA_DIR, 'cart.json'));
    const item = cart.items.find(item => item.id === id);
    
    if (!item) {
      return sendResponse(res, 404, null, '购物车商品不存在');
    }
    
    item.quantity = quantity;
    cart.total = cart.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
    
    if (writeJSONFile(path.join(DATA_DIR, 'cart.json'), cart)) {
      sendResponse(res, 200, cart, '更新购物车成功');
    } else {
      sendResponse(res, 500, null, '更新购物车失败');
    }
  } catch (error) {
    sendResponse(res, 400, null, '请求参数错误');
  }
};

const handleOptions = (req, res) => {
  res.writeHead(200, {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type'
  });
  res.end();
};

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const method = req.method;
  
  console.log(`${method} ${url.pathname}`);
  
  if (method === 'OPTIONS') {
    return handleOptions(req, res);
  }
  
  if (url.pathname === '/api/products' && method === 'GET') {
    return getProducts(req, res);
  }
  
  if (url.pathname.match(/^\/api\/products\/\d+$/) && method === 'GET') {
    return getProductById(req, res);
  }
  
  if (url.pathname.match(/^\/api\/products\/category\/\d+$/) && method === 'GET') {
    return getProductsByCategory(req, res);
  }
  
  if (url.pathname === '/api/categories' && method === 'GET') {
    return getCategories(req, res);
  }
  
  if (url.pathname === '/api/cart' && method === 'GET') {
    return getCart(req, res);
  }
  
  if (url.pathname === '/api/cart' && method === 'POST') {
    return addToCart(req, res);
  }
  
  if (url.pathname.match(/^\/api\/cart\/\d+$/) && method === 'DELETE') {
    return removeFromCart(req, res);
  }
  
  if (url.pathname.match(/^\/api\/cart\/\d+$/) && method === 'PUT') {
    return updateCartItem(req, res);
  }
  
  sendResponse(res, 404, null, '接口不存在');
});

server.listen(PORT, () => {
  console.log(`后端服务运行在 http://localhost:${PORT}`);
  console.log(`API 地址: http://localhost:${PORT}/api`);
});
