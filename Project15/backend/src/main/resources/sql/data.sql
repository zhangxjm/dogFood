USE job_recruitment;

INSERT INTO sys_user (username, password, role, status) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', 3, 1),
('hr1', 'e10adc3949ba59abbe56e057f20f883e', 2, 1),
('hr2', 'e10adc3949ba59abbe56e057f20f883e', 2, 1),
('jobseeker1', 'e10adc3949ba59abbe56e057f20f883e', 1, 1),
('jobseeker2', 'e10adc3949ba59abbe56e057f20f883e', 1, 1);

INSERT INTO company (user_id, company_name, industry, scale, address, description, contact_person, contact_phone, status) VALUES 
(2, '华为技术有限公司', '互联网/信息技术', '10000人以上', '广东省深圳市龙岗区坂田华为基地', '华为是全球领先的ICT（信息与通信）基础设施和智能终端提供商。', '张经理', '13800138001', 1),
(3, '阿里巴巴集团', '电子商务', '10000人以上', '浙江省杭州市余杭区文一西路969号', '阿里巴巴集团是全球领先的电子商务和云计算公司。', '李经理', '13800138002', 1);

INSERT INTO job (company_id, job_title, job_type, salary_min, salary_max, city, address, experience, education, job_description, job_requirement, welfare, status) VALUES 
(1, 'Java高级开发工程师', '全职', 25000, 45000, '深圳', '深圳市龙岗区坂田华为基地', '3-5年', '本科', '负责公司核心业务系统的设计与开发，参与技术架构设计与优化。', '1. 3年以上Java开发经验；2. 熟练掌握Spring Boot、MyBatis等框架；3. 有分布式系统开发经验优先。', '六险一金、年终奖金、弹性工作、免费班车、员工食堂', 1),
(1, '前端开发工程师', '全职', 20000, 35000, '深圳', '深圳市龙岗区坂田华为基地', '1-3年', '本科', '负责Web前端页面开发，与后端团队配合完成功能开发。', '1. 1年以上前端开发经验；2. 熟练掌握Vue/React框架；3. 熟悉HTML/CSS/JavaScript。', '六险一金、年终奖金、弹性工作、免费班车', 1),
(2, 'Java开发工程师', '全职', 20000, 40000, '杭州', '杭州市余杭区文一西路969号', '2-4年', '本科', '参与电商平台核心模块的开发与维护。', '1. 2年以上Java开发经验；2. 熟悉分布式系统、微服务架构；3. 有高并发系统经验优先。', '六险一金、股票期权、免费三餐、健身房', 1),
(2, '产品经理', '全职', 25000, 50000, '杭州', '杭州市余杭区文一西路969号', '3-5年', '本科', '负责电商产品的规划与设计，推动产品迭代。', '1. 3年以上产品经理经验；2. 有电商产品经验优先；3. 良好的数据分析能力。', '六险一金、股票期权、免费三餐、健身房', 1),
(1, '测试工程师', '全职', 15000, 25000, '深圳', '深圳市龙岗区坂田华为基地', '1-3年', '本科', '负责软件测试工作，编写测试用例，执行测试。', '1. 1年以上测试经验；2. 熟悉自动化测试工具优先；3. 良好的沟通能力。', '六险一金、年终奖金、弹性工作', 1);

INSERT INTO resume (user_id, real_name, gender, phone, email, city, job_intention, expected_salary_min, expected_salary_max, expected_city, education, work_experience, skills, self_evaluation, status) VALUES 
(4, '张三', 1, '13900139001', 'zhangsan@example.com', '深圳', 'Java开发工程师', 20000, 35000, '深圳', 
'[{"school":"深圳大学","major":"计算机科学与技术","degree":"本科","startTime":"2016-09","endTime":"2020-07"}]',
'[{"company":"科技有限公司","position":"Java开发工程师","startTime":"2020-08","endTime":"2023-06","description":"负责后端API开发"}]',
'Java,Spring Boot,MySQL,Redis',
'积极乐观，学习能力强，有团队协作精神。', 1),
(5, '李四', 0, '13900139002', 'lisi@example.com', '杭州', '前端开发工程师', 18000, 30000, '杭州', 
'[{"school":"浙江大学","major":"软件工程","degree":"本科","startTime":"2017-09","endTime":"2021-07"}]',
'[{"company":"互联网公司","position":"前端开发工程师","startTime":"2021-07","endTime":"2024-01","description":"负责Web前端开发"}]',
'Vue,React,JavaScript,CSS3',
'工作认真负责，注重细节，追求完美。', 1);
