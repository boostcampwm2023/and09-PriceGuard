import { Test, TestingModule } from '@nestjs/testing';
import { UsersService } from './user.service';
import { UsersRepository } from './user.repository';
import { User } from '../entities/user.entity';
import { UserDto } from 'src/dto/user.dto';
import { HttpException } from '@nestjs/common';

describe('UsersService', () => {
    let usersService: UsersService;
    let usersRepository: UsersRepository;
    const mockMemberRepository = {
        createUser: jest.fn(),
        findOne: jest.fn(),
    };
    beforeEach(async () => {
        const module: TestingModule = await Test.createTestingModule({
            providers: [
                UsersService,
                {
                    provide: UsersRepository,
                    useValue: mockMemberRepository,
                },
            ],
        }).compile();

        usersService = module.get<UsersService>(UsersService);
        usersRepository = module.get<UsersRepository>(UsersRepository);
    });

    it('should be defined', () => {
        expect(usersService).toBeDefined();
    });
    describe('registerUser', () => {
        const userDto: UserDto = {
            email: 'test@example.com',
            userName: 'testuser',
            password: 'password123',
        };
        it('user 회원가입', async () => {
            jest.spyOn(usersRepository, 'createUser').mockResolvedValue(new User());
            await expect(usersService.registerUser(userDto)).resolves.toBeInstanceOf(User);
            expect(usersRepository.createUser).toHaveBeenCalledWith(userDto);
        });

        it('email 중복 에러', async () => {
            jest.spyOn(usersRepository, 'createUser').mockRejectedValueOnce({ code: 'ER_DUP_ENTRY' });
            await expect(usersService.registerUser(userDto)).rejects.toThrow(HttpException);
            expect(usersRepository.createUser).toHaveBeenCalledWith(userDto);
        });

        it('유효 하지 않은 회원가입 입력값', async () => {
            jest.spyOn(usersRepository, 'createUser').mockRejectedValueOnce({ code: 'ER_NO_DEFAULT_FOR_FIELD' });
            await expect(usersService.registerUser(userDto)).rejects.toThrow(HttpException);
            expect(usersRepository.createUser).toHaveBeenCalledWith(userDto);
        });
    });

    describe('findOne', () => {
        it('email로 user 조회', async () => {
            const email = 'user@test.com';
            jest.spyOn(usersRepository, 'findOne').mockResolvedValue(new User());
            await expect(usersService.findOne(email)).resolves.toBeInstanceOf(User);
            expect(usersRepository.findOne).toHaveBeenCalledWith({ where: { email } });
        });

        it('해당 email로 회원가입한 유저 없는 경우', async () => {
            const email = 'user@test.com';
            jest.spyOn(usersRepository, 'findOne').mockResolvedValue(null);
            await expect(usersService.findOne(email)).resolves.toEqual(null);
            expect(usersRepository.findOne).toHaveBeenCalledWith({ where: { email } });
        });
    });

    describe('getUserById', () => {
        it('id로 user 찾기', async () => {
            const userId = 'testUUID';
            jest.spyOn(usersRepository, 'findOne').mockResolvedValue(new User());
            await expect(usersService.getUserById(userId)).resolves.toBeInstanceOf(User);
            expect(usersRepository.findOne).toHaveBeenCalledWith({ where: { id: userId } });
        });

        it('해당 id의 유저 없는 경우', async () => {
            const userId = 'testUUID';
            jest.spyOn(usersRepository, 'findOne').mockResolvedValue(null);
            await expect(usersService.findOne(userId)).resolves.toEqual(null);
            expect(usersRepository.findOne).toHaveBeenCalledWith({ where: { id: userId } });
        });
    });
});
