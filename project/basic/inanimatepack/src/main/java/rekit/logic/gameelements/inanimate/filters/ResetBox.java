package rekit.logic.gameelements.inanimate.filters;

import rekit.util.ReflectUtils.LoadMe;

/**
 * Realizes a {@link FilterBox} which resets all filters.
 *
 * @author Dominik Fuchss
 *
 */
@LoadMe
public final class ResetBox extends FilterBox {
	/**
	 * Prototype Constructor.
	 */
	public ResetBox() {
		super(null);
	}
}