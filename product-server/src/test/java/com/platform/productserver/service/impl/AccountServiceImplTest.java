package com.platform.productserver.service.impl;

import cn.hutool.core.util.ReflectUtil;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.mapper.AccountLogMapper;
import com.platform.productserver.mapper.AccountMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * 单元测试
 */
@RunWith(PowerMockRunner.class)
public class AccountServiceImplTest {

    @Mock
    private AccountMapper mockAccountMapper;
    @Mock
    private AccountLogMapper mockLogMapper;
    @Mock
    private TransactionTemplate mockTemplate;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectUtil.setFieldValue(accountService, "name", "33");
    }

    @Test
    public void testTrade() {
        // Setup
        final TradeDto tradeDto = new TradeDto();
        tradeDto.setTransId(0L);
        tradeDto.setAmount(new BigDecimal("0.00"));
        tradeDto.setRequestNo("requestNo");
        tradeDto.setOtherId(0L);
        tradeDto.setProdType("prodType");
        tradeDto.setTransType(0);
        tradeDto.setSource("source");
        tradeDto.setRemark("remark");

        // Run the test
        final boolean result = accountService.trade(tradeDto);

        // Verify the results
        assertThat(result).isTrue();
    }
}
