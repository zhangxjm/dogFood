INSERT INTO admins (username, password, nickname, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', 1);

INSERT INTO categories (name, icon, sort, status) VALUES
('手机数码', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=mobile%20phone%20icon%20minimalist%20blue&image_size=square', 1, 1),
('电脑办公', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=laptop%20computer%20icon%20minimalist%20blue&image_size=square', 2, 1),
('家用电器', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=home%20appliance%20icon%20minimalist%20blue&image_size=square', 3, 1),
('服饰鞋包', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fashion%20clothing%20icon%20minimalist%20blue&image_size=square', 4, 1),
('美妆个护', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beauty%20cosmetics%20icon%20minimalist%20blue&image_size=square', 5, 1),
('食品生鲜', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fresh%20food%20icon%20minimalist%20blue&image_size=square', 6, 1);

INSERT INTO products (category_id, name, description, main_image, sub_images, original_price, price, stock, sales, status) VALUES
(1, 'iPhone 15 Pro Max 256GB', 'A17 Pro芯片，钛金属设计，专业级摄影系统', 
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20Max%20smartphone%20elegant%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20Max%20smartphone%20side%20view&image_size=square_hd',
9999.00, 8999.00, 100, 520, 1),

(1, '华为Mate 60 Pro 512GB', '麒麟9000S芯片，卫星通话，超可靠玄武架构',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20Mate%2060%20Pro%20smartphone%20premium%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20Mate%2060%20Pro%20smartphone%20back%20view&image_size=square_hd',
7999.00, 6999.00, 80, 380, 1),

(2, 'MacBook Pro 14英寸 M3 Pro', 'M3 Pro芯片，18GB内存，512GB固态硬盘，Liquid Retina XDR显示屏',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%2014%20inch%20laptop%20elegant%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%2014%20inch%20laptop%20open%20view&image_size=square_hd',
16999.00, 14999.00, 50, 120, 1),

(2, '联想拯救者Y9000P 游戏本', 'i9-14900HX处理器，RTX4090显卡，32GB内存，1TB SSD',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lenovo%20Legion%20gaming%20laptop%20powerful%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=gaming%20laptop%20keyboard%20RGB%20lights&image_size=square_hd',
19999.00, 17999.00, 30, 85, 1),

(3, '戴森V15 Detect无绳吸尘器', '激光灰尘检测，智能感应，60分钟续航',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Dyson%20V15%20cordless%20vacuum%20cleaner%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=vacuum%20cleaner%20accessories%20set&image_size=square_hd',
5990.00, 4990.00, 60, 210, 1),

(3, '海尔十字对开门冰箱 500L', '变频无霜，智能温控，干湿分储',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Haier%20refrigerator%20double%20door%20modern%20kitchen&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=refrigerator%20interior%20fresh%20food&image_size=square_hd',
8999.00, 6999.00, 25, 95, 1),

(4, 'Nike Air Jordan 1 Retro High OG', '经典复刻，皮革鞋面，Air气垫缓震',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Nike%20Air%20Jordan%201%20sneakers%20black%20red%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Air%20Jordan%201%20sneakers%20side%20view&image_size=square_hd',
1499.00, 1299.00, 200, 650, 1),

(4, '优衣库男士羽绒服', '90%白鸭绒，轻便保暖，防风防水',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Uniqlo%20mens%20down%20jacket%20navy%20blue%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=down%20jacket%20wearing%20model&image_size=square_hd',
799.00, 599.00, 300, 820, 1),

(5, 'SK-II神仙水护肤精华露 230ml', 'PITERA精华，改善肤质，焕亮肌肤',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=SK-II%20Facial%20Treatment%20Essence%20bottle%20luxury%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=SK-II%20skincare%20products%20set&image_size=square_hd',
1590.00, 1290.00, 150, 450, 1),

(5, '兰蔻小黑瓶精华肌底液 100ml', '微生态护肤，修护肌底，强韧肌肤屏障',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lancome%20Advanced%20Genifique%20serum%20bottle%20elegant%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lancome%20skincare%20products%20display&image_size=square_hd',
1080.00, 899.00, 180, 380, 1),

(6, '智利进口车厘子 2斤装', 'JJ级，果径28-30mm，新鲜直达，甜蜜多汁',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fresh%20red%20cherries%20bowl%20fruit%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cherries%20close%20up%20water%20droplets&image_size=square_hd',
199.00, 149.00, 500, 1200, 1),

(6, '新西兰奇异果金果 12个装', '阳光金果，维C丰富，香甜多汁',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=golden%20kiwi%20fruit%20sliced%20fresh%20product%20photo&image_size=square_hd',
'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=kiwi%20fruit%20basket%20display&image_size=square_hd',
99.00, 69.00, 400, 980, 1);
