package com.lwx.core.utils.amt;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Author liuax01
 * @Date 2018/1/9 17:03
 */
public class UpperAmountUtilsTest {

	@Test
	public void test获取金额(){
		BigDecimal b1 = new BigDecimal(999.89);
		System.out.println(b1.toString());
		DecimalFormat df = new DecimalFormat("0.0#");
		System.out.println(df.format(b1));
		System.out.println(df.format(UpperAmountUtils.getFixBigDecimal(b1)));
	}

	@Test
	public void test大写(){
		BigDecimal b1 = new BigDecimal(10000000000100.00);
		System.out.println(b1.toString());
		System.out.println(UpperAmountUtils.convert2UpperMoney(b1));
	}
}
