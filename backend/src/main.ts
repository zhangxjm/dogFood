import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { AllExceptionsFilter } from './common/filters/all-exceptions.filter';
import { TransformInterceptor } from './common/interceptors/transform.interceptor';
import { ConfigService } from '@nestjs/config';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  
  // 全局配置
  app.setGlobalPrefix('api');
  app.useGlobalPipes(new ValidationPipe({
    whitelist: true,
    transform: true,
    forbidNonWhitelisted: true,
  }));
  
  // 全局异常过滤器
  app.useGlobalFilters(new AllExceptionsFilter());
  
  // 全局响应拦截器
  app.useGlobalInterceptors(new TransformInterceptor());
  
  // 跨域配置
  app.enableCors({
    origin: true,
    credentials: true,
  });
  
  // Swagger API 文档配置
  const config = new DocumentBuilder()
    .setTitle('Admin System API')
    .setDescription('Vue3 + NestJS + MySQL 全栈企业级后台管理系统 API 文档')
    .setVersion('1.0')
    .addBearerAuth()
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api-docs', app, document);
  
  const configService = app.get(ConfigService);
  const port = configService.get('APP_PORT') || 3000;
  
  await app.listen(port);
  console.log(`🚀 应用已启动: http://localhost:${port}`);
  console.log(`📚 API 文档: http://localhost:${port}/api-docs`);
}

bootstrap();
