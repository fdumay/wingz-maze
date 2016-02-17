package com.wingz.maze.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public final class JsonTestUtils {

	public static String asJson(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		return new ObjectMapper().writeValueAsString(value);
	}

	public static <T> T asObject(MvcResult mvcResult, Class<T> targetClass) throws Exception {
		String content = getContent(mvcResult);
		if(Strings.isNullOrEmpty(content)){
			return null;
		}
		return new ObjectMapper().readValue(content,targetClass);
	}

	public static <T> List<T> asList(MvcResult mvcResult, Class<T> targetClass) throws Exception {
		T[] array = asArray(mvcResult, targetClass);
		if (array == null) {
			return Collections.EMPTY_LIST;
		}
		return Lists.newArrayList(array);
	}

	public static <T> T[] asArray(MvcResult mvcResult, Class<T> targetClass) throws Exception {
		String content = getContent(mvcResult);
		if (Strings.isNullOrEmpty(content)) {
			return null;
		}
		Class<?> arrayClass = Class.forName("[L" + targetClass.getName() + ";");
		return (T[]) new ObjectMapper().readValue(content, arrayClass);
	}

	private static String getContent(MvcResult mvcResult) throws UnsupportedEncodingException {
		if (mvcResult == null) {
			return null;
		}
		MockHttpServletResponse reponse = mvcResult.getResponse();
		if (reponse == null) {
			return null;
		}
		return reponse.getContentAsString();
	}
	private JsonTestUtils() {
	}
}
