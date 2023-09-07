package com.platform.productserver.mock;

import com.alibaba.fastjson.JSONObject;
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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

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
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MockBusiness mockBusinessUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        // 使用反射的方式设置对象属性的值
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

        // mock 静态方法
        PowerMockito.mockStatic(IdGenUtils.class);
        PowerMockito.when(IdGenUtils.id()).thenReturn("2222");
        // mock 分布式锁
        RLock mock = mock(RLock.class);
        when(mock.tryLock(anyLong(), eq(TimeUnit.MINUTES))).thenReturn(Boolean.TRUE);
        when(mockRedisUtils.getLock(anyString())).thenReturn(mock);
        // mock http resethttp
        JSONObject body = new JSONObject();
        body.put("code", "0000");
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(JSONObject.class))).thenReturn(body);

        final User user = new User();
        user.setId(0L);
        user.setUserId("userId");
        user.setUsername("username");
        user.setEmail("email");
        user.setCellphone("cellphone");
        user.setStatus(0);
        user.setSeq(0L);
        // mock 业务查询和操作
        when(mockUserMapper.selectByUserId(anyString())).thenReturn(user, user, user);
        when(mockUserMapper.selectByUserId(anyString())).thenReturn(user).thenReturn(user).thenReturn(user);
        when(mockUserMapper.updateById(any(User.class))).thenReturn(1);
        // 模拟调用抛出异常
        // doThrow(RuntimeException.class).when(mockUserMapper).selectByUserId(anyString());

        // mock redis template
        ValueOperations operations = mock(ValueOperations.class);
        when(operations.increment(anyString())).thenReturn(230L);
        when(mockRedisTemplate.opsForValue()).thenReturn(operations);
        // mock transaction
        // when(mockTransaction.execute(any(TransactionCallback.class))).thenReturn(true);

        Answer<Object> answer = invocation -> {
            Object[] args = invocation.getArguments();
            TransactionCallback arg = (TransactionCallback) args[0];
            return arg.doInTransaction(new SimpleTransactionStatus());
        };
        when(mockTransaction.execute(any())).thenAnswer(answer);
        // when(mockTransaction.execute(Mockito.<TransactionCallback>any())).thenAnswer(new Answer<Object>() {
        //     public Object answer(InvocationOnMock invocation) {
        //         Object[] args = invocation.getArguments();
        //         TransactionCallback arg = (TransactionCallback) args[0];
        //         return arg.doInTransaction(new SimpleTransactionStatus());
        //     }
        // });
        // Run the test
        final UserDto result = mockBusinessUnderTest.mockTest(dto);

        // 断言返回对象不为空
        Assert.assertNotNull(result);
        // 断言结果的期望值和结果值相同
        Assert.assertEquals(result, expectedResult);
        // 断言方法中的某个环节被执行过 调用一次
        // times(n) 调用 n 次
        // never() 没有调用，相当于 调用 0 次 times(0)
        // atMostOnce() 最多调用一次
        // atLeastOnce() 最少调用一次
        // atLeast() 最少一次
        // atMost() 最多一次
        verify(mockUserMapper, atMost(10)).selectByUserId(anyString());
        verify(mockUserMapper, atLeast(1)).selectByUserId(anyString());
    }

    @Test(expected = RuntimeException.class)
    public void testMockTest_TransactionTemplateThrowsTransactionException() throws Exception {
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
        when(mockTransaction.execute(any(TransactionCallback.class))).thenThrow(RuntimeException.class);
        when(mockUserMapper.updateById(new User())).thenReturn(0);

        // Run the test
        final UserDto result = mockBusinessUnderTest.mockTest(dto);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
