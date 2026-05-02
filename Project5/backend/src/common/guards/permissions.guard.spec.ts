import { Test, TestingModule } from '@nestjs/testing';
import { Reflector } from '@nestjs/core';
import { ExecutionContext, HttpStatus } from '@nestjs/common';
import { PermissionsGuard } from './permissions.guard';
import { PERMISSIONS_KEY } from '@/common/decorators/permissions.decorator';

describe('PermissionsGuard', () => {
  let guard: PermissionsGuard;
  let reflector: jest.Mocked<Reflector>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PermissionsGuard,
        {
          provide: Reflector,
          useValue: {
            getAllAndOverride: jest.fn(),
          },
        },
      ],
    }).compile();

    guard = module.get<PermissionsGuard>(PermissionsGuard);
    reflector = module.get(Reflector) as jest.Mocked<Reflector>;
  });

  describe('canActivate', () => {
    it('should return true when no permissions required', () => {
      const mockRequest = {
        user: {
          userId: 1,
          username: 'admin',
        },
      };
      const mockContext = {
        switchToHttp: () => ({
          getRequest: () => mockRequest,
        }),
        getHandler: () => {},
        getClass: () => {},
      } as unknown as ExecutionContext;

      reflector.getAllAndOverride.mockReturnValue(undefined);

      const result = guard.canActivate(mockContext);

      expect(result).toBe(true);
    });

    it('should return false when user is not authenticated', () => {
      const mockRequest = {};
      const mockContext = {
        switchToHttp: () => ({
          getRequest: () => mockRequest,
        }),
        getHandler: () => {},
        getClass: () => {},
      } as unknown as ExecutionContext;

      reflector.getAllAndOverride.mockReturnValue(['system:user:query']);

      const result = guard.canActivate(mockContext);

      expect(result).toBe(false);
    });

    it('should return true for super admin', () => {
      const mockRequest = {
        user: {
          userId: 1,
          username: 'admin',
          roles: ['SUPER_ADMIN'],
          permissions: [],
        },
      };
      const mockContext = {
        switchToHttp: () => ({
          getRequest: () => mockRequest,
        }),
        getHandler: () => {},
        getClass: () => {},
      } as unknown as ExecutionContext;

      reflector.getAllAndOverride.mockReturnValue(['system:user:query']);

      const result = guard.canActivate(mockContext);

      expect(result).toBe(true);
    });

    it('should return true when user has required permission', () => {
      const mockRequest = {
        user: {
          userId: 2,
          username: 'user',
          roles: ['ADMIN'],
          permissions: ['system:user:query', 'system:user:add'],
        },
      };
      const mockContext = {
        switchToHttp: () => ({
          getRequest: () => mockRequest,
        }),
        getHandler: () => {},
        getClass: () => {},
      } as unknown as ExecutionContext;

      reflector.getAllAndOverride.mockReturnValue(['system:user:query']);

      const result = guard.canActivate(mockContext);

      expect(result).toBe(true);
    });

    it('should return false when user does not have required permission', () => {
      const mockRequest = {
        user: {
          userId: 2,
          username: 'user',
          roles: ['ADMIN'],
          permissions: ['system:user:query'],
        },
      };
      const mockContext = {
        switchToHttp: () => ({
          getRequest: () => mockRequest,
        }),
        getHandler: () => {},
        getClass: () => {},
      } as unknown as ExecutionContext;

      reflector.getAllAndOverride.mockReturnValue(['system:user:delete']);

      const result = guard.canActivate(mockContext);

      expect(result).toBe(false);
    });
  });
});
