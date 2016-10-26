package edu.kit.informatik.ragnarok.parser;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.informatik.ragnarok.primitives.image.RGBAColor;
import home.fox.visitors.Visitable;
import home.fox.visitors.parser.Parser;

/**
 * This {@link Parser} is used for parsing {@link RGBAColor RGBAColors}.
 *
 * @author Dominik Fuchss
 *
 */
public final class RGBAColorParser implements Parser {
	@Override
	public boolean parse(Visitable obj, Field field, String definition) throws Exception {
		if (!Parser.super.parse(obj, field, definition)) {
			return false;
		}
		Pattern pattern = Pattern.compile("([0-9]+),([0-9]+),([0-9]+),([0-9]+)");
		Matcher matcher = pattern.matcher(definition);
		if (!matcher.find()) {
			System.err.println("RGBColorParser: " + definition + " is no RBGA");
			return false;
		}

		int r = Integer.parseInt(matcher.group(1));
		int g = Integer.parseInt(matcher.group(2));
		int b = Integer.parseInt(matcher.group(3));
		int a = Integer.parseInt(matcher.group(3));

		field.set(obj, new RGBAColor(r, g, b, a));
		return true;
	}
}