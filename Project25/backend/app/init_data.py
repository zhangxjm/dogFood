from datetime import datetime, timedelta
from app.models import db, User, MovieCategory, Movie, Cinema, Hall, Schedule, Seat


def init_data():
    if User.query.count() == 0:
        admin = User(
            username='admin',
            email='admin@cinema.com',
            is_admin=True
        )
        admin.set_password('admin123')
        
        user = User(
            username='user',
            email='user@cinema.com',
            is_admin=False
        )
        user.set_password('user123')
        
        db.session.add_all([admin, user])
        db.session.commit()
        print('Default users created: admin/admin123, user/user123')
    
    if MovieCategory.query.count() == 0:
        categories = [
            MovieCategory(name='动作', description='动作电影'),
            MovieCategory(name='喜剧', description='喜剧电影'),
            MovieCategory(name='爱情', description='爱情电影'),
            MovieCategory(name='科幻', description='科幻电影'),
            MovieCategory(name='恐怖', description='恐怖电影'),
            MovieCategory(name='动画', description='动画电影')
        ]
        db.session.add_all(categories)
        db.session.commit()
        print('Movie categories created')
    
    if Cinema.query.count() == 0:
        cinema1 = Cinema(
            name='星影影城',
            address='北京市朝阳区建国路88号',
            phone='010-12345678',
            description='高端影院，4K激光放映'
        )
        cinema2 = Cinema(
            name='梦想影院',
            address='北京市海淀区中关村大街100号',
            phone='010-87654321',
            description='舒适观影体验'
        )
        db.session.add_all([cinema1, cinema2])
        db.session.commit()
        print('Cinemas created')
    
    if Hall.query.count() == 0:
        cinema1 = Cinema.query.first()
        cinema2 = Cinema.query.filter_by(name='梦想影院').first()
        
        halls = [
            Hall(name='1号厅', cinema_id=cinema1.id, rows=8, cols=10),
            Hall(name='2号厅', cinema_id=cinema1.id, rows=6, cols=8),
            Hall(name='3号IMAX厅', cinema_id=cinema1.id, rows=10, cols=12),
            Hall(name='1号厅', cinema_id=cinema2.id, rows=7, cols=9),
            Hall(name='2号厅', cinema_id=cinema2.id, rows=5, cols=7)
        ]
        db.session.add_all(halls)
        db.session.commit()
        
        for hall in halls:
            for row in range(1, hall.rows + 1):
                for col in range(1, hall.cols + 1):
                    seat = Seat(hall_id=hall.id, row_num=row, col_num=col)
                    db.session.add(seat)
        db.session.commit()
        print('Halls and seats created')
    
    if Movie.query.count() == 0:
        categories = MovieCategory.query.all()
        category_map = {c.name: c.id for c in categories}
        
        movies = [
            Movie(
                title='流浪地球3',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sci%20fi%20movie%20poster%20earth%20space%20travel&image_size=portrait_4_3',
                description='太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，流浪地球时代的年轻人再次挺身而出，展开争分夺秒的生死之战。',
                duration=173,
                release_date=datetime(2024, 1, 22),
                rating=8.5,
                director='郭帆',
                actors='吴京, 刘德华, 李雪健, 沙溢',
                category_id=category_map.get('科幻'),
                status='showing',
                is_hot=True
            ),
            Movie(
                title='热辣滚烫',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=comedy%20movie%20poster%20boxing%20woman%20strong&image_size=portrait_4_3',
                description='乐莹对生活已经失去了热情，在家庭聚会上被妹妹乐丹当众戳中痛点，她决心改变自己。她找到了拳击教练昊坤，开始了地狱式的拳击训练。',
                duration=129,
                release_date=datetime(2024, 2, 10),
                rating=7.8,
                director='贾玲',
                actors='贾玲, 雷佳音, 李雪琴, 沈涛',
                category_id=category_map.get('喜剧'),
                status='showing',
                is_hot=True
            ),
            Movie(
                title='飞驰人生2',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=racing%20movie%20poster%20car%20speed%20driver&image_size=portrait_4_3',
                description='曾经的顶级赛车手张弛，现在是驾校教练。尽管早已离开赛场，张弛内心的赛车梦从未熄灭。新一届巴音布鲁克拉力赛开启，张弛决定再赌一把。',
                duration=122,
                release_date=datetime(2024, 2, 10),
                rating=7.9,
                director='韩寒',
                actors='沈腾, 范丞丞, 尹正, 张本煜',
                category_id=category_map.get('喜剧'),
                status='showing',
                is_hot=True
            ),
            Movie(
                title='第二十条',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=drama%20movie%20poster%20courtroom%20justice%20law&image_size=portrait_4_3',
                description='韩明和妻子茂丽为了孩子能上个好学校，在市里买了房。然而邻居家的噪音让韩明一家人苦不堪言。韩明在工作中也遇到了棘手的案件。',
                duration=125,
                release_date=datetime(2024, 2, 10),
                rating=7.6,
                director='张艺谋',
                actors='雷佳音, 马丽, 赵丽颖, 高叶',
                category_id=category_map.get('爱情'),
                status='showing',
                is_hot=False
            ),
            Movie(
                title='哪吒之魔童闹海',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=animation%20movie%20poster%20nezha%20chinese%20mythology&image_size=portrait_4_3',
                description='哪吒虽成为灵珠转世，但其魔丸身份却逐渐显露，他的魔性在逐渐觉醒。而此时，天庭也在密切关注着哪吒的动向。',
                duration=110,
                release_date=datetime(2025, 1, 22),
                rating=9.2,
                director='饺子',
                actors='吕艳婷, 囧森瑟夫, 瀚墨, 陈浩',
                category_id=category_map.get('动画'),
                status='coming',
                is_hot=True
            ),
            Movie(
                title='唐人街探案4',
                poster='https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=detective%20movie%20poster%20chinatown%20mystery%20comedy&image_size=portrait_4_3',
                description='唐仁和秦风接到一个新案件，前往东京调查一桩离奇的谋杀案。在调查过程中，他们发现案件背后隐藏着更大的阴谋。',
                duration=136,
                release_date=datetime(2025, 2, 1),
                rating=7.5,
                director='陈思诚',
                actors='王宝强, 刘昊然, 妻夫木聪, 托尼贾',
                category_id=category_map.get('动作'),
                status='coming',
                is_hot=True
            )
        ]
        db.session.add_all(movies)
        db.session.commit()
        print('Movies created')
    
    if Schedule.query.count() == 0:
        movies = Movie.query.filter_by(status='showing').all()
        halls = Hall.query.all()
        
        base_time = datetime.now().replace(hour=10, minute=0, second=0, microsecond=0)
        
        schedules = []
        for movie in movies[:3]:
            for i, hall in enumerate(halls[:2]):
                for day_offset in range(3):
                    for time_offset in [0, 3, 6]:
                        start_time = base_time + timedelta(days=day_offset, hours=time_offset)
                        end_time = start_time + timedelta(minutes=movie.duration + 15)
                        schedule = Schedule(
                            movie_id=movie.id,
                            hall_id=hall.id,
                            start_time=start_time,
                            end_time=end_time,
                            price=50.0 + (time_offset * 5)
                        )
                        schedules.append(schedule)
        
        db.session.add_all(schedules)
        db.session.commit()
        print('Schedules created')
    
    print('Initialization complete!')
