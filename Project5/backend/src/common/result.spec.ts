import { Result, StatusCode } from './result';

describe('Result', () => {
  describe('static success', () => {
    it('should create success result with data and default message', () => {
      const data = { id: 1, name: 'Test' };
      const result = Result.success(data);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(StatusCode.SUCCESS);
      expect(result.data).toEqual(data);
      expect(result.message).toBe('操作成功');
      expect(result.timestamp).toBeDefined();
    });

    it('should create success result with custom message', () => {
      const data = { id: 1, name: 'Test' };
      const result = Result.success(data, '自定义成功消息');

      expect(result.message).toBe('自定义成功消息');
    });

    it('should create success result with undefined data', () => {
      const result = Result.success();

      expect(result.code).toBe(StatusCode.SUCCESS);
      expect(result.data).toBeUndefined();
    });
  });

  describe('static error', () => {
    it('should create error result with default message and code', () => {
      const result = Result.error();

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(StatusCode.ERROR);
      expect(result.data).toBeNull();
      expect(result.message).toBe('操作失败');
    });

    it('should create error result with custom message and code', () => {
      const result = Result.error('自定义错误消息', StatusCode.NOT_FOUND);

      expect(result.message).toBe('自定义错误消息');
      expect(result.code).toBe(StatusCode.NOT_FOUND);
    });
  });

  describe('static unauthorized', () => {
    it('should create unauthorized result with default message', () => {
      const result = Result.unauthorized();

      expect(result.code).toBe(StatusCode.UNAUTHORIZED);
      expect(result.data).toBeNull();
      expect(result.message).toBe('未授权访问');
    });

    it('should create unauthorized result with custom message', () => {
      const result = Result.unauthorized('Token已过期');

      expect(result.message).toBe('Token已过期');
    });
  });

  describe('static forbidden', () => {
    it('should create forbidden result with default message', () => {
      const result = Result.forbidden();

      expect(result.code).toBe(StatusCode.FORBIDDEN);
      expect(result.data).toBeNull();
      expect(result.message).toBe('权限不足');
    });

    it('should create forbidden result with custom message', () => {
      const result = Result.forbidden('没有此操作权限');

      expect(result.message).toBe('没有此操作权限');
    });
  });

  describe('constructor', () => {
    it('should create result with specified values', () => {
      const code = StatusCode.INTERNAL_SERVER_ERROR;
      const message = '服务器内部错误';
      const data = { error: 'Something went wrong' };

      const result = new Result(code, message, data);

      expect(result.code).toBe(code);
      expect(result.message).toBe(message);
      expect(result.data).toEqual(data);
      expect(result.timestamp).toBeDefined();
      expect(typeof result.timestamp).toBe('number');
    });
  });

  describe('StatusCode enum', () => {
    it('should have correct values', () => {
      expect(StatusCode.SUCCESS).toBe(0);
      expect(StatusCode.ERROR).toBe(-1);
      expect(StatusCode.UNAUTHORIZED).toBe(401);
      expect(StatusCode.FORBIDDEN).toBe(403);
      expect(StatusCode.NOT_FOUND).toBe(404);
      expect(StatusCode.INTERNAL_SERVER_ERROR).toBe(500);
    });
  });
});
