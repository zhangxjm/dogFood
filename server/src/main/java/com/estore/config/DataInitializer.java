package com.estore.config;

import com.estore.entity.Admin;
import com.estore.entity.Category;
import com.estore.entity.Product;
import com.estore.repository.AdminRepository;
import com.estore.repository.CategoryRepository;
import com.estore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        initCategories();
        initProducts();
    }
    
    private void initAdmin() {
        adminRepository.findByUsername("admin").ifPresentOrElse(
            admin -> {
                if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    adminRepository.save(admin);
                    log.info("重置管理员密码: admin / admin123");
                }
            },
            () -> {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("系统管理员");
                admin.setStatus(1);
                adminRepository.save(admin);
                log.info("初始化管理员账号: admin / admin123");
            }
        );
    }
    
    private void initCategories() {
        if (categoryRepository.count() == 0) {
            String[] categories = {"手机数码", "电脑办公", "家用电器", "服饰鞋包", "美妆个护", "食品生鲜"};
            for (int i = 0; i < categories.length; i++) {
                Category category = new Category();
                category.setName(categories[i]);
                category.setSort(i + 1);
                category.setStatus(1);
                categoryRepository.save(category);
            }
            log.info("初始化商品分类: {} 个", categories.length);
        }
    }
    
    private void initProducts() {
        if (productRepository.count() == 0) {
            createProduct(1L, "iPhone 15 Pro Max 256GB", "A17 Pro芯片，钛金属设计，专业级摄影系统", 
                    new BigDecimal("9999.00"), new BigDecimal("8999.00"), 100, 520);
            createProduct(1L, "华为Mate 60 Pro 512GB", "麒麟9000S芯片，卫星通话，超可靠玄武架构",
                    new BigDecimal("7999.00"), new BigDecimal("6999.00"), 80, 380);
            createProduct(2L, "MacBook Pro 14英寸 M3 Pro", "M3 Pro芯片，18GB内存，512GB固态硬盘",
                    new BigDecimal("16999.00"), new BigDecimal("14999.00"), 50, 120);
            createProduct(2L, "联想拯救者Y9000P 游戏本", "i9-14900HX处理器，RTX4090显卡",
                    new BigDecimal("19999.00"), new BigDecimal("17999.00"), 30, 85);
            createProduct(3L, "戴森V15 Detect无绳吸尘器", "激光灰尘检测，智能感应，60分钟续航",
                    new BigDecimal("5990.00"), new BigDecimal("4990.00"), 60, 210);
            createProduct(3L, "海尔十字对开门冰箱 500L", "变频无霜，智能温控，干湿分储",
                    new BigDecimal("8999.00"), new BigDecimal("6999.00"), 25, 95);
            createProduct(4L, "Nike Air Jordan 1 Retro High OG", "经典复刻，皮革鞋面，Air气垫缓震",
                    new BigDecimal("1499.00"), new BigDecimal("1299.00"), 200, 650);
            createProduct(4L, "优衣库男士羽绒服", "90%白鸭绒，轻便保暖，防风防水",
                    new BigDecimal("799.00"), new BigDecimal("599.00"), 300, 820);
            createProduct(5L, "SK-II神仙水护肤精华露 230ml", "PITERA精华，改善肤质，焕亮肌肤",
                    new BigDecimal("1590.00"), new BigDecimal("1290.00"), 150, 450);
            createProduct(5L, "兰蔻小黑瓶精华肌底液 100ml", "微生态护肤，修护肌底，强韧肌肤屏障",
                    new BigDecimal("1080.00"), new BigDecimal("899.00"), 180, 380);
            createProduct(6L, "智利进口车厘子 2斤装", "JJ级，果径28-30mm，新鲜直达，甜蜜多汁",
                    new BigDecimal("199.00"), new BigDecimal("149.00"), 500, 1200);
            createProduct(6L, "新西兰奇异果金果 12个装", "阳光金果，维C丰富，香甜多汁",
                    new BigDecimal("99.00"), new BigDecimal("69.00"), 400, 980);
            
            log.info("初始化测试商品: 12 个");
        }
    }
    
    private void createProduct(Long categoryId, String name, String description,
                                BigDecimal originalPrice, BigDecimal price,
                                Integer stock, Integer sales) {
        Product product = new Product();
        product.setCategoryId(categoryId);
        product.setName(name);
        product.setDescription(description);
        product.setOriginalPrice(originalPrice);
        product.setPrice(price);
        product.setStock(stock);
        product.setSales(sales);
        product.setStatus(1);
        product.setIsDeleted(0);
        productRepository.save(product);
    }
}
