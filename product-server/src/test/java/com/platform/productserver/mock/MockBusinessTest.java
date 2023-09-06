package com.platform.productserver.mock;

import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.UserDto;
import com.platform.productserver.entity.User;
import com.platform.productserver.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@Slf4j
@PrepareForTest(value = {IdGenUtils.class})
@RunWith(PowerMockRunner.class)
public class MockBusinessTest {

    @Mock
    private RedisTemplate mockRedisTemplate;
    @Mock
    private RedisUtils mockRedisUtils;
    @Mock
    private TransactionTemplate mockTransaction;
    @Mock
    private UserMapper mockUserMapper;

    @InjectMocks
    private MockBusiness mockBusinessUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(mockBusinessUnderTest, "name", "test");
    }

    @Test
    public void testMockTest() throws Exception {
        // Setup
        final UserDto dto = new UserDto();
        dto.setId(0L);
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setRoles(Lists.newArrayList("value"));

        final UserDto expectedResult = new UserDto();
        expectedResult.setId(0L);
        expectedResult.setUsername("username");
        expectedResult.setPassword("password");
        expectedResult.setRoles(Lists.newArrayList("value"));

        // PowerMockito.mockStatic(SpringContextUtil.class);
        PowerMockito.mockStatic(IdGenUtils.class);
        PowerMockito.when(IdGenUtils.id()).thenReturn("2222");

        RLock mock = mock(RLock.class);
        when(mock.tryLock(anyLong(), eq(TimeUnit.MINUTES))).thenReturn(Boolean.TRUE);
        when(mockRedisUtils.getLock(anyString())).thenReturn(mock);

        final User user = new User();
        user.setId(0L);
        user.setUserId("userId");
        user.setUsername("username");
        user.setEmail("email");
        user.setCellphone("cellphone");
        user.setStatus(0);
        user.setSeq(0L);
        when(mockUserMapper.selectByUserId(anyString())).thenReturn(user);

        ValueOperations operations = mock(ValueOperations.class);
        when(operations.increment(anyString())).thenReturn(230L);
        when(mockRedisTemplate.opsForValue()).thenReturn(operations);

        when(mockTransaction.execute(any(TransactionCallback.class))).thenReturn(true);
        // 可以是 any ,但是不能为 null
        when(mockUserMapper.updateById(any(User.class))).thenReturn(0);

        // Run the test
        final UserDto result = mockBusinessUnderTest.mockTest(dto);

        // Verify the results
        Assert.assertNotNull(result);
    }

    @Test
    public void testMocktest_TransactionTemplateThrowsTransactionException() throws Exception {
        // Setup
        final UserDto dto = new UserDto();
        dto.setId(0L);
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setRoles(Arrays.asList("value"));

        final UserDto expectedResult = new UserDto();
        expectedResult.setId(0L);
        expectedResult.setUsername("username");
        expectedResult.setPassword("password");
        expectedResult.setRoles(Arrays.asList("value"));

        when(mockRedisUtils.getLock("key")).thenReturn(null);

        // Configure UserMapper.selectByUserId(...).
        final User user = new User();
        user.setId(0L);
        user.setUserId("userId");
        user.setUsername("username");
        user.setEmail("email");
        user.setCellphone("cellphone");
        user.setStatus(0);
        user.setSeq(0L);
        user.setCreateTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        user.setUpdateTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        when(mockUserMapper.selectByUserId("userId")).thenReturn(user);

        when(mockRedisTemplate.opsForValue()).thenReturn(null);
        when(mockTransaction.execute(any(TransactionCallback.class))).thenThrow(TransactionException.class);
        when(mockUserMapper.updateById(new User())).thenReturn(0);

        // Run the test
        final UserDto result = mockBusinessUnderTest.mockTest(dto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
