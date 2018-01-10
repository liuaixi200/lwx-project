package com.lwx.core.utils.amt;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @Author liuax01
 * @Date 2018/1/9 12:10
 */
public class UpperAmountUtils {

	//零壹贰叁肆伍陆柒捌玖
	private static char[] UPPER_DIGIT = {'零', '壹','贰', '叁','肆','伍','陆','柒','捌','玖'};

	//亿 万 千佰拾元角分
	private static String UNIT = "万千佰拾亿千佰拾万千佰拾元角分";
	private static String UNIT_V = "9999999999999.99";

	public static String convert2UpperMoney(BigDecimal bd){

		if(bd == null || bd.compareTo(new BigDecimal("0")) == 0){
			return "零元整";
		} else {
			//如果是double构造可能会有问题  比如 999.89
			bd = getFixBigDecimal(bd);
			String value = Long.toString(bd.movePointRight(2).longValue());
			System.out.println(value);
			//最大金额万亿
			if(value.length() > UNIT.length()){
				throw new RuntimeException("超出最大金额["+UNIT_V+"]");
			}
			if(bd.compareTo(new BigDecimal("0")) <0 ){
				throw new RuntimeException("金额不能小于0");
			}
			StringBuilder sb = new StringBuilder("");
			int subLenght = UNIT.length()-value.length();
			for(int i = 0; i < value.length(); i++){
				char v1 = value.charAt(i);
				sb.append(UPPER_DIGIT[v1-'0']);
				char u1 = UNIT.charAt(subLenght++);
				if(v1 == '0'){
					if(u1 == '元' || u1 == '角' || u1 == '分' || u1 == '万' || u1 == '亿'){
						sb.append(u1);
					}

				} else {
					sb.append(u1);
				}
			}
			int total = sb.length();
			String res = sb.toString();
			while (res.indexOf("零零")>-1){
				res = res.replaceAll("零零","零");
			}
			res = res.replace("零元","元");
			res = res.replace("零角","");
			res = res.replace("零分","");
			res = res.replace("零万","万");
			res = res.replace("零亿","亿");
			res = res.replace("亿万","亿");
			return res;
		}
	}

	/**
	 * 返回一个保留两位小数的数字
	 * @param bd
	 * @return
	 */
	public static BigDecimal getFixBigDecimal(BigDecimal bd){
		if(bd == null){
			return new BigDecimal("0.00");
		} else {
			return bd.setScale(2, RoundingMode.HALF_UP);
		}

	}
}
